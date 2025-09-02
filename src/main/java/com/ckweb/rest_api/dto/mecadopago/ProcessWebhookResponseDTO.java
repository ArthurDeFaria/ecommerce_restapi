package com.ckweb.rest_api.dto.mecadopago;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProcessWebhookResponseDTO {
    boolean success;
    String status;
}
