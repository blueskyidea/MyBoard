package me.haneul.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostDTO {
    List<Long> id;
    private String title;
    private String contents;

}
