package tech.qihangec.api.common.wei.bo;

import lombok.Data;

@Data
public class GoodsListApiBo {
    private Integer status;
    private Integer page_size;
    private String next_key;

}
