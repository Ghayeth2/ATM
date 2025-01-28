package com.atm.business.abstracts;

import com.atm.model.dtos.EmailDetails;

public interface EmailServices {
    void sendEmail(EmailDetails emailDetails);
}
