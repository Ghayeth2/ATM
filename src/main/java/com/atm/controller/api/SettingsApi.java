package com.atm.controller.api;

import com.atm.business.abstracts.SettingsServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController @Log4j2
@RequestMapping("/atm/api/settings")
public class SettingsApi {
    private final SettingsServices settingsServices;

    @PostMapping("/update")
    public ResponseEntity<?> updateAndGet(@RequestBody Map<String, String>
                                          request) {
        request.forEach(
                (key, value) -> System.out.println(key
                +":"+value)
        );
        return new ResponseEntity<>(settingsServices
                .updateAndGetAll(request), HttpStatus.OK);
    }

    @GetMapping("/values")
    public ResponseEntity<?> valuesSettings() {
        log.info("SettingsApi -> valuesSettings is running");
        return new ResponseEntity<>(settingsServices
                .getAllValues(), HttpStatus.OK);
    }

}
