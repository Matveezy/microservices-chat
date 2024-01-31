package org.example.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Pageable request")
public class PageableRequest {

    @Min(0)
    private Integer page;

    @Min(1)
    @Max(50)
    private Integer size;

    public static Pageable getPageable(PageableRequest pageableRequest) {
        if (pageableRequest == null) return Pageable.unpaged();
        else return PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize());
    }

    public static Pageable getPageable(PageableRequest pageableRequest, String sortBy) {
        var sorting = Sort.by(Sort.Direction.ASC, sortBy);
        if (pageableRequest == null) return Pageable.unpaged(sorting);
        else return PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), sorting);
    }
}