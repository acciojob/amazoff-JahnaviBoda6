package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Repository
public class OrderRepository {

    HashMap<String,Order> orderDb = new HashMap<>();
    HashMap<String,DeliveryPartner> partnerDb = new HashMap<>();
    HashMap<String, List<String>> orderPartnerPairDb = new HashMap<>();

    public void addOrder(Order order){
        orderDb.put(order.getId(),order);
    }
    public void addPartner(String partnerId){
        partnerDb.put(partnerId,new DeliveryPartner(partnerId));
        orderPartnerPairDb.put(partnerId,new ArrayList<>());
    }
    public void addOrderPartnerPair(String orderId,String partnerId){
        partnerDb.get(partnerId).setNumberOfOrders(partnerDb.get(partnerId).getNumberOfOrders()+1);
        List<String> partnerOrders;
        if(orderPartnerPairDb.containsKey(partnerId))
            partnerOrders = orderPartnerPairDb.get(partnerId);
        else
            partnerOrders = new ArrayList<>();
        partnerOrders.add(orderId);
        orderPartnerPairDb.put(partnerId,partnerOrders);
    }
    public Order getOrderById(String orderId){
        return orderDb.get(orderId);
    }
    public DeliveryPartner getPartnerById(String partnerId){
        return partnerDb.get(partnerId);
    }
    public int getOrderCountByPartnerId(String partnerId){
        return partnerDb.get(partnerId).getNumberOfOrders();
    }
    public List<String> getOrdersByPartnerId(String partnerId){
        return orderPartnerPairDb.get(partnerId);
    }
    public List<String> getAllOrders(){
        return new ArrayList<>(orderDb.keySet());
    }
    public int getCountOfUnassignedOrders(){
        int assignedOrdersCount = 0;
        for(List<String> orderList: orderPartnerPairDb.values()){
            assignedOrdersCount+=orderList.size();
        }
        return orderDb.size() - assignedOrdersCount;
    }
    public int getOrdersLeftAfterGivenTimeByPartnerId(String time,String partnerId){
        int ordersLeftAfterGivenTime = 0;
        int givenTime = Integer.parseInt(time.split(":")[0])*60;
        givenTime += Integer.parseInt(time.split(":")[1]);
        for(String orderId: orderPartnerPairDb.get(partnerId)){
            if(givenTime < orderDb.get(orderId).getDeliveryTime()) ordersLeftAfterGivenTime++;
        }
        return ordersLeftAfterGivenTime;
    }
    public int getLastDeliveryTimeByPartnerId(String partnerId){
        int mx = Integer.MIN_VALUE;
        for(String orderId : orderPartnerPairDb.get(partnerId)){
            mx = Math.max(mx,orderDb.get(orderId).getDeliveryTime());
        }
        return mx;
    }
    public void deletePartnerById(String partnerId){
        orderPartnerPairDb.remove(partnerId);
        partnerDb.remove(partnerId);
    }
    public void deleteOrderById(String orderId){
        orderDb.remove(orderId);
        for(List<String> orderList: orderPartnerPairDb.values()){
            for(int i=0;i<orderList.size();i++){
                if(orderList.get(i).equals(orderId)){
                    orderList.remove(i);
                    break;
                }
            }
        }
    }
}