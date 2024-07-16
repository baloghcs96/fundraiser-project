package hu.progmasters.fundraiser.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Grade {

    BRONZE("GRADE_BRONZE", 0, 1000, 0, 1),
    SILVER("GRADE_SILVER", 1001, 2000, 0, 2),
    GOLD("GRADE_GOLD", 2001, 3000, 10, 3),
    PLATINUM("GRADE_PLATINUM", 5000, Integer.MAX_VALUE, 15, 4);

    private final String name;
    private final int minPoints;
    private final int maxPoints;
    private final int discount;
    private final int value;

}
