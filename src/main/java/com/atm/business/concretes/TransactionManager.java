package com.atm.business.concretes;

import com.atm.business.abstracts.AccountServices;
import com.atm.business.abstracts.ConfigService;
import com.atm.business.abstracts.TransactionsServices;
import com.atm.core.exceptions.AccountsCurrenciesMismatchException;
import com.atm.core.exceptions.InsufficientFundsException;
import com.atm.core.utils.strings_generators.StringGenerator;
import com.atm.dao.daos.TransactionDao;
import com.atm.model.dtos.TransactionContext;
import com.atm.model.dtos.payloads.records.responses.ReceiptData;
import com.atm.model.dtos.payloads.records.responses.TransactionDto;
import com.atm.model.dtos.payloads.records.responses.UserAccountTransaction;
import com.atm.model.entities.Account;
import com.atm.model.entities.Transaction;
import com.atm.business.strategies.abstracts.TransactionsStrategy;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import org.thymeleaf.context.Context;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service("transactionsManager")
public class TransactionManager implements TransactionsServices {

    private final AccountServices accountServices;
    private final ConfigService configService;

    private final Map<String, TransactionsStrategy> strategies;
    private final TransactionDao transactionDao;

    public TransactionManager(@Qualifier("depositStrategy")
                              TransactionsStrategy deposit,
                              @Qualifier("withdrawalStrategy")
                              TransactionsStrategy withdraw,
                              @Qualifier("transferStrategy")
                              TransactionsStrategy transfer,
                              AccountServices accountServices,
                              ConfigService configService, TransactionDao transactionDao) {
        this.configService = configService;
        this.accountServices = accountServices;
        strategies = Map.of(
                "Deposit", deposit,
                "Withdrawal", withdraw,
                "Transfer", transfer
        );
        this.transactionDao = transactionDao;
    }

    @Override
    public Page<UserAccountTransaction> findAllByUser(Long userId) {
        return null;
    }

    @Override
    public Page<TransactionDto> findAllByAccount(String accountSlug) {
        return null;
    }

    // TODO: try applying SRP on services relate to newTransaction

    /**
     * Will call the proper transaction strategy and execute transaction,
     * Will create report for the transaction (receipt pdf),
     * Will create new Transaction record and insert it to database.
     * Fees will only be calculated for Transfer.
     * If want to display the fees, then call them from .properties config file
     * @param type
     * @param
     * @return
     */


    @Override @SneakyThrows
    public String newTransaction(String type, String amountRequest, String... numbers) {
        // If amount <= 0, throw InsufficientFundsException
        double amount = Double.parseDouble(amountRequest);
        if (amount <= 0) {
            throw new InsufficientFundsException("Amount must be greater than 0");
        }
        // For transfer, if receiver's account currency differs Block it.
        if (!numbers[0].isEmpty() || !numbers[0].isBlank())
            if (!doCurrenciesMatch(numbers[0], numbers[1]))
                throw new AccountsCurrenciesMismatchException(
                        "Currency of receiver account does " +
                                "not match currencies of transaction"
                );

        // Preparing context & calling transaction strategy
        TransactionContext transactionContext = TransactionContext
                .builder().amount(amount).sender(numbers[0]).receiver(numbers[1])
                .build();
        double balanceAfter = strategies.get(type).execute(transactionContext);
        // Calling config method to set TemplateEngine to be used
        TemplateEngine templateEngine = getTemplateEngine();
        // Setting and getting Context (Date record, prepare in there, send or get data from there)
        Context context = processReceiptData(type, amount, numbers);
        // Processing & Generating Template as String
        String template = templateEngine.process("receipt", context);
        // Creating receipt
        String receiptUrl = createReceipt(template, numbers[1]);
        // Create & save Transaction record
        // Retrieving target account
        Transaction transaction = Transaction.builder().type(type)
                .receiptUrl(receiptUrl).balanceAfter(balanceAfter)
                .amount(amount).build();
        transaction.setSlug(new StringGenerator().slug(receiptUrl));
        // If sender number exists => Transfer, otherwise => withdraw / deposit
        // If Transfer: sender is the parent account, else: receiver is parent
        if (numbers[0].isEmpty() || numbers[0].isBlank()) {
            Account receiver = accountServices.findByNumber(numbers[1]);
            // Setting parent account
            transaction.setAccount(receiver);
        } else {
            Account sender = accountServices.findByNumber(numbers[0]);
            // Setting parent account
            transaction.setAccount(sender);
        }
        // Saving Transaction record
        Transaction saved = transactionDao.save(transaction);
        // Writing & creating the template / Handling exceptions
        // Creating Transaction Model and populating its data
        // Saving the Model to database. (TransactionDao)
        return "Transaction is saved: "+saved.getType();
    }

    private boolean doCurrenciesMatch(String sender, String receiver) {
        Account senderAccount = accountServices.findByNumber(sender);
        Account receiverAccount = accountServices.findByNumber(receiver);
        return senderAccount.getCurrency().equals(receiverAccount.getCurrency());
    }

    @SneakyThrows // IOException
    private String createReceipt(String template, String accountNumber) {
        // Preparing name of the file, dynamic outPutDir (path) from .properties + name
        String subNumber = accountNumber.substring(6, 11);
        String fileName = "receipt_" + subNumber + "_" + new StringGenerator().randomString(5)
                +".html";
        String outPutPath = configService.getProperties().getProperty("receipts.path");
        System.out.println(outPutPath);
        // Checking if outPutDir exists - if not create it
        Path url = Paths.get(outPutPath);
        Files.createDirectories(url);
        Path directory = url;
        Path filePath = directory.resolve(fileName);
        // Writing template to outPutDirectory
        Files.write(filePath, template.getBytes());
        return fileName;
    }

    @SneakyThrows
    private Context processReceiptData(String type, double amount, String... numbers) {
        // TODO: Accounts (number, type & user's fullName)
        // Preparing data to place finally to ReceiptData object before the Context
        // Retrieving accounts Sender (number, type, FullName)
        Account sender = null;
        if (!numbers[0].isEmpty() && !numbers[1].isBlank())
            sender = accountServices.findByNumber(numbers[0]);
        // receiver account's (number, type, and user's fullName)
        Account receiver = accountServices.findByNumber(numbers[1]);
        // Calculating fees by Types **** start
        // If both accounts are for different users calculate fee
        double feeAmount = 0;
        if (sender != null && !sender.getUser().getId()
                .equals(receiver.getUser().getId())) {
            // Calculate fee for business account
            if (sender.getType().contains("Business")) {
                double businessFee = Double.parseDouble(
                        configService.getProperties().getProperty("fees.business")
                );
                feeAmount = amount * businessFee;
                // Formatting the result to .2f
                feeAmount = Math.round(feeAmount * 100) / 100.0;
            }
            // Calculate fee for savings account
            if (sender.getType().contains("Savings")) {
                double savingsFee = Double.parseDouble(
                        configService.getProperties().getProperty("fees.savings")
                );
                feeAmount = amount * savingsFee;
                // Formatting the result to .2f
                feeAmount = Math.round(feeAmount * 100) / 100.0;
            }
            // Calculate fee for personal account
            if (sender.getType().contains("Personal")) {
                double personalFee = Double.parseDouble(
                        configService.getProperties().getProperty("fees.personal")
                );
                feeAmount = amount * personalFee;
                // Formatting the result to .2f
                feeAmount = Math.round(feeAmount * 100) / 100.0;
            }
        }
        // Total amount (amount + fee)
        double totalAmount = amount + feeAmount;
        // Formatting result of double to .2f
        totalAmount = Math.round(totalAmount * 100) / 100.0;
        // Formatting amount value to .2f
        amount = Math.round(amount * 100) / 100.0;
        // Generating today's date
        LocalDateTime localDateTime = LocalDateTime.now();
        String date = localDateTime.format(DateTimeFormatter
                .ofPattern("dd-MM-yyyy HH:mm:ss"));
        // Generating QR transaction code
        String qrCode = new StringGenerator().randomString(6);
        // Assigning values for ReceiptData object
        // If withdraw & deposit, no sender
        ReceiptData receipt;
        if (sender == null) {
            receipt = new ReceiptData(
                    "",
                    receiver.getUser().getFirstName()+" "
                    +receiver.getUser().getLastName(),
                    "",
                    receiver.getNumber(),
                    "",
                    receiver.getType(),
                    type, date, qrCode, receiver.getCurrency(),
                    amount, feeAmount, totalAmount
            );
        } else {
            receipt = new ReceiptData(
                    sender.getUser().getFirstName() + " "
                            + sender.getUser().getLastName(),
                    receiver.getUser().getFirstName()+ " "
                            + receiver.getUser().getLastName(),
                    sender.getNumber(), receiver.getNumber(),
                    sender.getType(), receiver.getType(),
                    type, date, qrCode, sender.getCurrency(),
                    amount, feeAmount, totalAmount
            );
        }
        // Setting the object into Context
        Context context = new Context();
        context.setVariable("receipt", receipt);
        return context;
        // TODO: Fees calculations
        // TODO: Today's date formatted
        // TODO: Transaction qr utils method
        // TODO: Setting to object, then context and return.
    }

    private TemplateEngine getTemplateEngine() {
        TemplateEngine templateEngine = new SpringTemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/receipt_template/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateEngine.setTemplateResolver(templateResolver);
        return templateEngine;
    }

}
