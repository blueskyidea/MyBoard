package me.haneul.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentDTO {
    private Long parentid;
    private String contents;
}
