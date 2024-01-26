package dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BiblePPTDownResponseDtoList {
    // 성경구절
    private List<BiblePPTDownResponseDto> biblePPTDownResponseDtoList;
}
