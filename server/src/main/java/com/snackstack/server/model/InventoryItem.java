package com.snackstack.server.model;

import java.time.Instant;

public record InventoryItem(
    Integer itemId,
    Integer userId,
    String ingredientName,
    Instant purchaseDate
) {

}
