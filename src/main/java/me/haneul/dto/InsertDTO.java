package me.haneul.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.haneul.entity.Board;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InsertDTO {
    private Long id;
    private String title;
    private String contents;
}
