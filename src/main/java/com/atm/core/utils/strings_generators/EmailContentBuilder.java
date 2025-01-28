package com.atm.core.utils.strings_generators;

/**
 * For building emails' content, with proper introduction,
 * and avoiding repeating.
 */
public class EmailContentBuilder {
    public String buildBody(String firstName,
                            String subject,
                            String link) {
        return " <p>\n" +
                "        Hello " + firstName + ", you're receiving this email to " + subject + ".\n" +
                "         <br>\n" +
                "        Please, follow the link bellow to " + subject + ". <br>\n" +
                "        <a href=\"" + link + "\">" + subject + "</a>\n" +
                "    </p>";
    }
}
