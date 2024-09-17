package com.atm.core.utils.strings_generators;

import com.atm.business.abstracts.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Random;

@Component
public class AccountNumberGenerator {
    @Autowired
    private ConfigService configService;
    StringBuilder sb = new StringBuilder();
    Random rand = new Random();

    @Value("${account.number.lead}")
    private String leadNumber;
    @Value("${account.number.tail}")
    private String tailNumber;

    public String accountNumber() throws IOException {
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

        // Updating Tail & Lead values
        configService.updateLeadTailNumbers(leadNumber, tailNumber);
        return sb.toString();
    }
}
