package com.atm.core.audit;

import java.util.Optional;

    public class AuditorAwareTest extends AuditorAwareImpl{
   @Override
   public Optional<String> getCurrentAuditor() {
       return Optional.of("test");
   }
}
