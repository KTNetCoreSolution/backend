// VersionController.java
package com.boot.ktn.controller.ver;

import com.boot.ktn.config.VersionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.base.path}/ver")
public class VersionController {

    @Autowired
    private VersionManager versionManager;

    @PostMapping("/check")
    public ResponseEntity<String> checkVersion() {
        String serverVersion = versionManager.getServerVersion();
        return ResponseEntity.ok()
                .header("X-Server-Version", serverVersion)
                .body(serverVersion);
    }
}