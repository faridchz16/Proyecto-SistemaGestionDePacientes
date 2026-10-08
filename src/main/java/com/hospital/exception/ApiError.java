package com.hospital.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiError(LocalDateTime timestamp, int status, String error, String mensaje,
                       String path, Map<String, String> errores) {
}
