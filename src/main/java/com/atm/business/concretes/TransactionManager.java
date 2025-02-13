package com.atm.business.concretes;

import com.atm.business.abstracts.AccountServices;
import com.atm.business.abstracts.ConfigService;
import com.atm.business.abstracts.TransactionsServices;
import com.atm.core.exceptions.AccountsCurrenciesMismatchException;
import com.atm.core.utils.converter.DateFormatConverter;
import com.atm.core.utils.strings_generators.StringGenerator;
import com.atm.dao.criterias.TransactionsCriteria;
import com.atm.dao.daos.TransactionDao;
import com.atm.model.dtos.TransactionContext;
import com.atm.model.dtos.payloads.records.requests.TransactionsCriteriaRequest;
import com.atm.model.dtos.payloads.records.requests.TransactionsFiltersRequest;
import com.atm.model.dtos.payloads.records.responses.ReceiptData;
import com.atm.model.dtos.payloads.responses.TransactionDto;
import com.atm.model.dtos.payloads.responses.UserAccountTransaction;
import com.atm.model.entities.Account;
import com.atm.model.entities.Transaction;
import com.atm.business.strategies.abstracts.TransactionsStrategy;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.List;
import java.util.Map;

@Slf4j
@Service("transactionsManager")
public class TransactionManager implements TransactionsServices {

    private final AccountServices accountServices;
    private final ConfigService configService;
    private final TransactionsCriteria criteria;

    private final Map<String, TransactionsStrategy> strategies;
    private final TransactionDao transactionDao;

    @Value("${server.port}")
    private int port;

    @Value("${server.remote.host}")
    private String remoteHost;

    @Value("${server.local.host}")
    private String localHost;

    public TransactionManager(@Qualifier("depositStrategy")
                              TransactionsStrategy deposit,
                              @Qualifier("withdrawalStrategy")
                              TransactionsStrategy withdraw,
                              @Qualifier("transferStrategy")
                              TransactionsStrategy transfer,
                              AccountServices accountServices,
                              ConfigService configService,
                              TransactionDao transactionDao,
                              TransactionsCriteria criteria) {
        this.configService = configService;
        this.accountServices = accountServices;
        this.criteria = criteria;
        strategies = Map.of(
                "Deposit", deposit,
                "Withdrawal", withdraw,
                "Transfer", transfer
        );
        this.transactionDao = transactionDao;
    }

    /**
     * Returns Users (fullName, email) >
     * Accounts (type) >
     * Transactions (type, amount, balanceAfter, date),
     * with filtering feature.
     *
     * @param filters
     * @return
     */
    // TODO: the controller will be an API. (Criteria API)
    @Override
    @SneakyThrows
    public Page<UserAccountTransaction> findAllFiltered(
            TransactionsFiltersRequest
                    filters) {
        // Retrieving page size for transactions from config
        int pageSize = Integer.parseInt(configService.getProperties()
                .getProperty("users.page.size"));
        // Setting created date formats
        LocalDateTime startDate;
        LocalDateTime endDate;
        if (filters.fromDate().isEmpty() && filters.toDate().isEmpty()) {
            startDate = new DateFormatConverter()
                    .formatRequestDate(LocalDateTime.now().minusMonths(1));
            endDate = new DateFormatConverter().formatRequestDate(
                    LocalDateTime.now()
            );
        } else {
            startDate = new DateFormatConverter().formatRequestDate(
                    filters.fromDate()
            );
            endDate = new DateFormatConverter().formatRequestDate(
                    filters.toDate()
            );
        }
        // Setting Default & Custom sortBy & Order
        String sortBy;
        String sortOrder;
        if (filters.sortBy().isEmpty() && filters.sortOrder().isEmpty()) {
            sortBy = "createdDate";
            sortOrder = "desc";
        } else {
            sortBy = filters.sortBy();
            sortOrder = filters.sortOrder();
        }
        // Paging data
        Pageable pageable = PageRequest.of(filters.page() - 1,
                pageSize);
        // Preparing request data
        TransactionsCriteriaRequest req =
                new TransactionsCriteriaRequest(
                        filters.searchQuery(),
                        sortBy,
                        sortOrder,
                        startDate,
                        endDate,
                        pageable,
                        filters.fromAmount(),
                        filters.toAmount()
                );
        // Calling the criteria repository
        return criteria.findAll(req).map(
                tr -> UserAccountTransaction.builder()
                        .email(tr.getEmail())
                        .accountType(tr.getAccountType())
                        .transactionType(tr.getTransactionType())
                        .fullName(tr.getFullName())
                        .amount(tr.getAmount())
                        .balanceAfter(tr.getBalanceAfter())
                        .formattedDate(
                                new DateFormatConverter()
                                        .formatDate(
                                                tr.getDate()
                                        )
                        ).build()
        );
    }

    // TODO: the controller will be an API. (Pages, sort, and one filter element)
    @Override
    @SneakyThrows
    public Page<TransactionDto> findAllByAccount(String accountSlug,
                                                 String startDate,
                                                 String endDate,
                                                 int page,
                                                 String sortOrder,
                                                 String sortBy) {
        // Host, port, Receipt Url builder credentials
        String localProtocol = "http://";
        String remoteProtocol = "https://";
        // Retrieving the account
        Account account = accountServices.findBySlug(accountSlug);
        // Default date or set date settings
        LocalDateTime startingDate;
        LocalDateTime endingDate;
        if (!startDate.isEmpty() && !endDate.isEmpty()) {
            startingDate = new DateFormatConverter().formatRequestDate(startDate);
            endingDate = new DateFormatConverter().formatRequestDate(endDate);
        } else {
            // Default between a month from now (ago)
            startingDate = new DateFormatConverter()
                    .formatRequestDate(LocalDateTime.now().minusMonths(1));
            endingDate = new DateFormatConverter()
                    .formatRequestDate(LocalDateTime.now());
        }
        // Parsing current page & getting limit from .properties
        int pageSize = Integer.parseInt(configService.getProperties()
                .getProperty("transactions.page.size"));
        // Preparing Sort order & by Default is by date descending
        Sort sort;
        if (!sortBy.isEmpty() && !sortOrder.isEmpty())
            sort = sortOrder.equalsIgnoreCase("asc") ?
                    Sort.by(sortBy).ascending() :
                    Sort.by(sortBy).descending();
        else
            sort = Sort.by("createdDate").descending();
        // Pageable
        Pageable pageable = PageRequest.of(page - 1,
                pageSize, sort);
        System.out.println("Account data: " +
                account.getId() + " "
                + account.getNumber());
        // Calling dao, formatting date, mapping result back to data object
        return transactionDao.findAllByAccount(
                        account.getId(),
                        startingDate,
                        endingDate,
                        pageable)
                .map(tr -> TransactionDto.builder()
                       .formattedDate(
                               new DateFormatConverter()
                                       .formatDate(tr.getCreatedDate())
                       )
                       .amount(tr.getAmount())
                       .balanceAfter(tr.getBalanceAfter())
                       .slug(tr.getSlug())
                       .type(tr.getType())
                       .receiptUrl(
                               localProtocol + localHost + ":" + port +
                                       "/receipts" + "/" + tr.getReceiptUrl())
                       .build()
                );
    }

    // TODO: try applying SRP on services relate to newTransaction

    /**
     * Will call the proper transaction strategy and execute transaction,
     * Will create report for the transaction (receipt pdf),
     * Will create new Transaction record and insert it to database.
     * Fees will only be calculated for Transfer.
     * If want to display the fees, then call them from .properties config file
     *
     * @param type
     * @param
     * @return
     */


    @Override
    @SneakyThrows
    public String newTransaction(String type, String amountRequest, String... numbers) {
        double amount = Double.parseDouble(amountRequest);
        // For transfer, if receiver's account currency differs Block it.
        if (!numbers[0].isEmpty() || !numbers[0].isBlank())
            if (!doCurrenciesMatch(numbers[0], numbers[1])) {
                log.info("Throwing AccountsCurrenciesMismatchException");
                throw new AccountsCurrenciesMismatchException(
                        "Currency of receiver account does " +
                                "not match currencies of transaction"
                );
            }

        // Preparing context & calling transaction strategy
        TransactionContext transactionContext = TransactionContext
                .builder().amount(amount).sender(numbers[0]).receiver(numbers[1])
                .build();
        double[] balanceAfter = strategies.get(type).execute(transactionContext);
        // Calling config method to set TemplateEngine to be used
        TemplateEngine templateEngine = getTemplateEngine();
        // Setting and getting Context (Date record, prepare in there, send or get data from there)
        Context context = processReceiptData(type, amount, numbers);
        // Processing & Generating Template as String
        String template = templateEngine.process("receipt", context);
        // Creating receipt
        String receiptUrl = createReceipt(template, numbers[1]);
        // If transfer do the following
        // If sender number exists => Transfer, otherwise => withdraw / deposit
        // If Transfer: sender is the parenta account, else: receiver is parent
        if (!numbers[0].isEmpty() || !numbers[0].isBlank()) {
            log.info("TransactionManager -> TransferStrategy -> create & save two transactions...");
            // Retrieving accounts
            System.out.println(numbers[0]+ " " +numbers[1]);
            Account senderAccount = accountServices.findByNumber(numbers[0]);
            Account receiverAccount = accountServices.findByNumber(numbers[1]);
            // Sender account transaction
            Transaction sender = Transaction.builder().type(type)
                    .receiptUrl(receiptUrl).amount(Math.round(amount * 100) / 100.0)
                    .balanceAfter(Math.round(balanceAfter[0] * 100) / 100.0)
                    .account(senderAccount).build();
            sender.setSlug(new StringGenerator().slug(receiptUrl));
            // Receiver account transaction
            Transaction receiver = Transaction.builder().type(type)
                    .receiptUrl(receiptUrl).amount(Math.round(amount * 100) / 100.0)
                    .balanceAfter(Math.round(balanceAfter[1] * 100) / 100.0)
                    .account(receiverAccount).build();
            receiver.setSlug(new StringGenerator().slug(receiptUrl+numbers[1]));
            // Save the two transactions
            transactionDao.saveAll(List.of(sender, receiver));
            // Return to controller, skip the rest of the method
            return "Transaction is saved: " + type;
        }
        // Create & save Transaction record
        // Retrieving target account (withdraw / deposit)
        Account account = accountServices.findByNumber(numbers[1]);
        Transaction transaction = Transaction.builder().type(type)
                .receiptUrl(receiptUrl).balanceAfter(
                        Math.round(balanceAfter[0] * 100) / 100.0)
                .amount(Math.round(amount * 100) / 100.0).account(account).build();
        transaction.setSlug(new StringGenerator().slug(receiptUrl));
        // Saving Transaction record
        Transaction saved = transactionDao.save(transaction);
        // Writing & creating the template / Handling exceptions
        // Creating Transaction Model and populating its data
        // Saving the Model to database. (TransactionDao)
        return "Transaction is saved: " + saved.getType();
    }

    private boolean doCurrenciesMatch(String sender, String receiver) {
        Account senderAccount = accountServices.findByNumber(sender);
        Account receiverAccount = accountServices.findByNumber(receiver);
        boolean isMatch = senderAccount.getCurrency().equals(receiverAccount.getCurrency());
        log.info("TransactionManager -> newTransaction -> doCurrenciesMatch: " + isMatch);
        return isMatch;
    }

    @SneakyThrows // IOException
    private String createReceipt(String template, String accountNumber) {
        // Preparing name of the file, dynamic outPutDir (path) from .properties + name
        String subNumber = accountNumber.substring(6, 11);
        log.info("Creating receipt file..");
        String fileName = "receipt_" + subNumber + "_" + new StringGenerator().randomString(5)
                + ".html";
        log.info("path of receipts where it will be, is being extracted...");
        String outPutPath = configService.getProperties().getProperty("transactions.receipts.path");
        System.out.println(outPutPath);
        log.info("Output path: " + outPutPath);
        // Checking if outPutDir exists - if not create it
        log.info("Checking if output directory exists...");
        Path url = Paths.get(outPutPath);
        Files.createDirectories(url);
        Path directory = url;
        log.info("Resolving file's path name...");
        Path filePath = directory.resolve(fileName);
        // Writing template to outPutDirectory
        Files.write(filePath, template.getBytes());
        log.info("File is written to output path.");
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
                        configService.getProperties().getProperty("transactions.fees.business")
                );
                feeAmount = amount * businessFee;
                // Formatting the result to .2f
                feeAmount = Math.round(feeAmount * 100) / 100.0;
            }
            // Calculate fee for savings account
            if (sender.getType().contains("Savings")) {
                double savingsFee = Double.parseDouble(
                        configService.getProperties().getProperty("transactions.fees.savings")
                );
                feeAmount = amount * savingsFee;
                // Formatting the result to .2f
                feeAmount = Math.round(feeAmount * 100) / 100.0;
            }
            // Calculate fee for personal account
            if (sender.getType().contains("Personal")) {
                double personalFee = Double.parseDouble(
                        configService.getProperties().getProperty("transactions.fees.personal")
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
                    receiver.getUser().getFirstName() + " "
                            + receiver.getUser().getLastName(),
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
                    receiver.getUser().getFirstName() + " "
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
