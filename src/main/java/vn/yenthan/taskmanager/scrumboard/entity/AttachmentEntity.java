package vn.yenthan.taskmanager.scrumboard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.yenthan.taskmanager.core.util.EntityBase;

@Entity
@Table(name = "tbl_attachment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentEntity extends EntityBase {

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_lastModified", nullable = false)
    private Long fileLastModified;

    @Column(name = "file_lastModifiedDate", nullable = false)
    private String fileLastModifiedDate;

    @Column(name = "preview", nullable = false)
    private String preview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "card_id", nullable = false)
    private CardEntity card;
}
