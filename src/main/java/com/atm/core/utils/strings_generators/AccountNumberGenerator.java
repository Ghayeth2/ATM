package com.atm.core.utils.strings_generators;

import com.atm.business.abstracts.ConfigService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;
@Log4j2
@Component
@RequiredArgsConstructor
public class AccountNumberGenerator {

    private final ConfigService configService;

    public String accountNumber() throws IOException {
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        Properties prop = configService.getProperties();
//        log.info("Key - Value pairs: "+prop);
        String leadNumber = prop.getProperty("account.lead.number");
        String tailNumber = prop.getProperty("account.tail.number");
//        log.info("before generating account number: " + leadNumber + " " + tailNumber );
        sb.setLength(0);
        sb.append(leadNumber)
                .append("-");
        for (int i = 0; i < 2; i++){
            for (int j = 0; j < 4; j++){
                int digit = rand.nextInt(10);
                sb.append(digit);
            }
            sb.append("-");
        }
        sb.append(tailNumber);
        log.info("numbers in number generator "+leadNumber+" "+tailNumber);
        // Updating Tail & Lead values
        configService.updateLeadTailNumbers(leadNumber, tailNumber);
        return sb.toString();
    }
}
