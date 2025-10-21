package vn.yenthan.taskmanager.scrumboard.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateListRequest {
    @NotNull(message = "List ID is required")
    private Long id;

    @NotBlank(message = "List name is required")
    @Size(max = 255, message = "List name must not exceed 255 characters")
    private String name;
}
