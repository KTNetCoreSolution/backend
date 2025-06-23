package com.boot.ktn.model.license;

import lombok.Data;
import java.util.List;

@Data
public class LicenseResponse {
    private List<LicenseInfo> licenses;
}
