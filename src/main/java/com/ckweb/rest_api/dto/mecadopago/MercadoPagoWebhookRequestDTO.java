package com.ckweb.rest_api.dto.mecadopago;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class MercadoPagoWebhookRequestDTO {
    private String action;
    private String api_version;
    private MPData data;
    private String data_created;
    private Long id;
    private boolean live_mode;
    private String type;
    private Long user_id;

    @Data
    @Getter
    @Setter
    @NoArgsConstructor
    public static class MPData {
        private String id;
    }
}