package com.switchfully.eurder.api.dtos;

import java.util.List;

public record OrderReportDto(int id, List<ItemGroupReportDto> itemGroups, Double orderPrice) {
}
