package vn.yenthan.taskmanager.scrumboard.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {
    private Long id;
    private String title;
    private List<AttachmentDto> attachments;
    private List<LabelDto> label;
    private String date;
    private List<Object> comments; // Empty array for now
    private String desc;
    private List<MemberDto> members;
    private List<CheckedListDto> checkedList;
    private Long laneId;
}
