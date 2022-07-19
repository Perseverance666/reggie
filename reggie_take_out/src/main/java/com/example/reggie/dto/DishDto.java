package com.example.reggie.dto;

import com.example.reggie.entity.backend.Dish;
import com.example.reggie.entity.backend.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
