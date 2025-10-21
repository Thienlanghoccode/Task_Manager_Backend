package vn.yenthan.taskmanager.scrumboard.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCardRequest {
    @NotBlank(message = "Card title is required")
    @Size(max = 255, message = "Card title must not exceed 255 characters")
    private String title;

    private String description;
    private String date;
    private Long laneId;
    private List<Long> memberIds;
    private List<Long> labelIds;
}
