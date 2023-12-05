package cn.lyn4ever.export2md.halo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Content  {
    private String raw;
    private String content;
    private String rawType;

}
