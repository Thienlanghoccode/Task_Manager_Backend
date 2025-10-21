package vn.yenthan.taskmanager.scrumboard.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCardCategoryRequest {
    @NotNull(message = "Card ID is required")
    private Long cardId;

    @NotNull(message = "New lane ID is required")
    private Long laneId;
}
