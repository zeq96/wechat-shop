package com.andrew.wechetshop.api.generate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Order implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ORDER_TABLE.ID
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ORDER_TABLE.USER_ID
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    private Long userId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ORDER_TABLE.TOTAL_PRICE
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    private BigDecimal totalPrice;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ORDER_TABLE.ADDRESS
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    private String address;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ORDER_TABLE.EXPRESS_COMPANY
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    private String expressCompany;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ORDER_TABLE.EXPRESS_ID
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    private String expressId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ORDER_TABLE.STATUS
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    private String status;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ORDER_TABLE.CREATED_AT
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    private Date createdAt;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ORDER_TABLE.UPDATED_AT
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    private Date updatedAt;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ORDER_TABLE.SHOP_ID
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    private Long shopId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table ORDER_TABLE
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ORDER_TABLE.ID
     *
     * @return the value of ORDER_TABLE.ID
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ORDER_TABLE.ID
     *
     * @param id the value for ORDER_TABLE.ID
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ORDER_TABLE.USER_ID
     *
     * @return the value of ORDER_TABLE.USER_ID
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ORDER_TABLE.USER_ID
     *
     * @param userId the value for ORDER_TABLE.USER_ID
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ORDER_TABLE.TOTAL_PRICE
     *
     * @return the value of ORDER_TABLE.TOTAL_PRICE
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ORDER_TABLE.TOTAL_PRICE
     *
     * @param totalPrice the value for ORDER_TABLE.TOTAL_PRICE
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ORDER_TABLE.ADDRESS
     *
     * @return the value of ORDER_TABLE.ADDRESS
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    public String getAddress() {
        return address;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ORDER_TABLE.ADDRESS
     *
     * @param address the value for ORDER_TABLE.ADDRESS
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ORDER_TABLE.EXPRESS_COMPANY
     *
     * @return the value of ORDER_TABLE.EXPRESS_COMPANY
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    public String getExpressCompany() {
        return expressCompany;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ORDER_TABLE.EXPRESS_COMPANY
     *
     * @param expressCompany the value for ORDER_TABLE.EXPRESS_COMPANY
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    public void setExpressCompany(String expressCompany) {
        this.expressCompany = expressCompany == null ? null : expressCompany.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ORDER_TABLE.EXPRESS_ID
     *
     * @return the value of ORDER_TABLE.EXPRESS_ID
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    public String getExpressId() {
        return expressId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ORDER_TABLE.EXPRESS_ID
     *
     * @param expressId the value for ORDER_TABLE.EXPRESS_ID
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    public void setExpressId(String expressId) {
        this.expressId = expressId == null ? null : expressId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ORDER_TABLE.STATUS
     *
     * @return the value of ORDER_TABLE.STATUS
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    public String getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ORDER_TABLE.STATUS
     *
     * @param status the value for ORDER_TABLE.STATUS
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ORDER_TABLE.CREATED_AT
     *
     * @return the value of ORDER_TABLE.CREATED_AT
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ORDER_TABLE.CREATED_AT
     *
     * @param createdAt the value for ORDER_TABLE.CREATED_AT
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ORDER_TABLE.UPDATED_AT
     *
     * @return the value of ORDER_TABLE.UPDATED_AT
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ORDER_TABLE.UPDATED_AT
     *
     * @param updatedAt the value for ORDER_TABLE.UPDATED_AT
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ORDER_TABLE.SHOP_ID
     *
     * @return the value of ORDER_TABLE.SHOP_ID
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    public Long getShopId() {
        return shopId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ORDER_TABLE.SHOP_ID
     *
     * @param shopId the value for ORDER_TABLE.SHOP_ID
     *
     * @mbg.generated Wed Sep 21 11:53:06 CST 2022
     */
    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }
}