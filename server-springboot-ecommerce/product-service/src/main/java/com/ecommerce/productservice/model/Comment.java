package com.ecommerce.productservice.model;


import com.ecommerce.common.model.AdvanceBaseModal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.UUID;

@Entity(name = "comments")
@Table
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder
public class Comment extends AdvanceBaseModal {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;
    private String text;
    @ManyToOne()
    @JoinColumn(name = "product_id")
    private Product product;

    private String creator;
}
