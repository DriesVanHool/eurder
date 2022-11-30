package com.switchfully.eurder.api.dtos;

import java.util.List;

public record TotalOrderReportDto(List<OrderReportDto> orders, double totalPrice) {
}
