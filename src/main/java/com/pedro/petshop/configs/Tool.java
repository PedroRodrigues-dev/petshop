package com.pedro.petshop.configs;

import java.util.Arrays;
import java.util.Objects;

import org.springframework.beans.BeanUtils;

public class Tool {
    public static String[] getNullPropertyNames(Object source) {
        return Arrays.stream(BeanUtils.getPropertyDescriptors(source.getClass()))
                .map(pd -> {
                    try {
                        return pd.getReadMethod() != null && pd.getReadMethod().invoke(source) == null ? pd.getName()
                                : null;
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray(String[]::new);
    }
}
