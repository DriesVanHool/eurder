package com.switchfully.eurder.api.dtos;

import java.util.List;

public record OrderReportDto(String id, List<ItemGroupReportDto> itemGroups, Double orderPrice) {
}
