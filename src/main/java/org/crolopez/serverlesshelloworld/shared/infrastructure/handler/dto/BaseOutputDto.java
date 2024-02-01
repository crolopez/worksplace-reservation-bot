package org.crolopez.serverlesshelloworld.shared.infrastructure.handler.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@AllArgsConstructor
@NoArgsConstructor
@Data
@With
public class BaseOutputDto {

    private String result;

    private String requestId;
}
