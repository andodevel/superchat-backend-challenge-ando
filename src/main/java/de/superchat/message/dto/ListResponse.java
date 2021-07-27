package de.superchat.message.dto;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *  Wrap list of DTO in pagination object.
 *
 * @param <T> type extends Serializable
 */
@Data
@AllArgsConstructor
public class ListResponse<T extends Serializable> {

    private Long total;
    private Integer pageCount;
    private Integer pageIndex;
    private Integer pageSize;
    private List<T> results;

}
