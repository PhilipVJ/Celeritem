package com.example.celeritem.Model;

public class Order {
    private OrderBy orderBy;
    private OrderDirection direction;

    public Order(OrderBy orderBy, OrderDirection direction) {
        this.direction = direction;
        this.orderBy = orderBy;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public OrderDirection getOrderDirection() {
        return direction;
    }

    public void switchOrderDirection(){
        if(direction == OrderDirection.ASCENDING){
            direction = OrderDirection.DESCENDING;
        }
        else{
            direction = OrderDirection.ASCENDING;
        }
    }
}
