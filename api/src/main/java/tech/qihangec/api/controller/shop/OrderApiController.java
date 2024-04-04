package tech.qihangec.api.controller.shop;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tech.qihangec.api.common.AjaxResult;
import tech.qihangec.api.common.ResultVoEnum;
import tech.qihangec.api.common.enums.EnumShopType;
import tech.qihangec.api.common.enums.HttpStatus;
import tech.qihangec.api.common.utils.StringUtils;
import tech.qihangec.api.common.wei.ApiCommon;
import tech.qihangec.api.common.wei.PullRequest;
import tech.qihangec.api.common.wei.RemoteUtil;
import tech.qihangec.api.common.wei.bo.CreateTimeRangeBo;
import tech.qihangec.api.common.wei.bo.OrderDetailBo;
import tech.qihangec.api.common.wei.bo.OrderListBo;
import tech.qihangec.api.common.wei.service.OrderApiService;
import tech.qihangec.api.common.wei.vo.OrderDetailVo;
import tech.qihangec.api.common.wei.vo.OrderListVo;
import tech.qihangec.api.common.wei.vo.OrderVoDeliverInfoAddress;
import tech.qihangec.api.domain.SysShopPullLasttime;
import tech.qihangec.api.domain.SysShopPullLogs;
import tech.qihangec.api.domain.WeiOrder;
import tech.qihangec.api.domain.WeiOrderItem;
import tech.qihangec.api.service.SysShopPullLasttimeService;
import tech.qihangec.api.service.SysShopPullLogsService;
import tech.qihangec.api.service.WeiOrderService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequestMapping("/shop/order")
@RestController
@AllArgsConstructor
public class OrderApiController {
    private final ApiCommon apiCommon;

    private final WeiOrderService weiOrderService;
    private final SysShopPullLogsService pullLogsService;
    private final SysShopPullLasttimeService pullLasttimeService;
    /**
     * 拉取订单
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/pull_list", method = RequestMethod.POST)
    public AjaxResult pullList(@RequestBody PullRequest params) throws Exception {
        if (params.getShopId() == null || params.getShopId() <= 0) {
            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "参数错误，没有店铺Id");
        }

        Date currDateTime = new Date();
        long beginTime = System.currentTimeMillis();

        var checkResult = apiCommon.checkBefore(params.getShopId());
        if (checkResult.getCode() != ResultVoEnum.SUCCESS.getIndex()) {
            return AjaxResult.error(checkResult.getCode(), checkResult.getMsg(),checkResult.getData());
        }
        String accessToken = checkResult.getData().getAccessToken();
        String serverUrl = checkResult.getData().getServerUrl();
        String appKey = checkResult.getData().getAppKey();
        String appSecret = checkResult.getData().getAppSecret();

        // 获取最后更新时间
        LocalDateTime startTime = null;
        LocalDateTime  endTime = null;
        SysShopPullLasttime lasttime = pullLasttimeService.getLasttimeByShop(params.getShopId(), "ORDER");
        if(lasttime == null){
            endTime = LocalDateTime.now();
            startTime = endTime.minusDays(1);
        }else{
            startTime = lasttime.getLasttime().minusHours(1);//取上次结束一个小时前
            endTime = startTime.plusDays(1);//取24小时
            if(endTime.isAfter(LocalDateTime.now())){
                endTime = LocalDateTime.now();
            }
        }


        OrderApiService remoting = RemoteUtil.Remoting(serverUrl, OrderApiService.class);
        OrderListBo apiBo = new OrderListBo();
        apiBo.setPage_size(100);
        CreateTimeRangeBo tbo= new CreateTimeRangeBo();
        tbo.setStart_time(startTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
        tbo.setEnd_time(endTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
        apiBo.setCreate_time_range(tbo);

        OrderListVo orderList = remoting.getOrderList(accessToken, apiBo);
        int insertSuccess = 0;//新增成功的订单
        int totalError = 0;
        int hasExistOrder = 0;//已存在的订单数

        if(orderList.getErrcode() == 0) {
            // 拉取到了数据 拉取详情
            if(orderList.getOrder_id_list()!=null&&orderList.getOrder_id_list().length>0) {
                for (var orderId : orderList.getOrder_id_list()) {
                    OrderDetailBo bo = new OrderDetailBo();
                    bo.setOrder_id(orderId.toString());
                    OrderDetailVo orderDetail = remoting.getOrderDetail(accessToken, bo);
                    if(orderDetail.getErrcode() == 0){
                        WeiOrder order = new WeiOrder();
                        order.setOrderId(orderDetail.getOrder().getOrder_id());
                        order.setShopId(params.getShopId());
                        order.setOpenid(orderDetail.getOrder().getOpenid());
                        order.setCreateTime(orderDetail.getOrder().getCreate_time());
                        order.setUpdateTime(orderDetail.getOrder().getUpdate_time());
                        order.setUnionid(orderDetail.getOrder().getUnionid());
                        order.setStatus(orderDetail.getOrder().getStatus());
                        order.setAftersaleDetail(JSONObject.toJSONString(orderDetail.getOrder().getAftersale_detail()));
                        order.setPayInfo(JSONObject.toJSONString(orderDetail.getOrder().getOrder_detail().getPay_info()));
                        order.setProductPrice(orderDetail.getOrder().getOrder_detail().getPrice_info().getInteger("product_price"));
                        order.setOrderPrice(orderDetail.getOrder().getOrder_detail().getPrice_info().getInteger("order_price"));
                        order.setFreight(orderDetail.getOrder().getOrder_detail().getPrice_info().getInteger("freight"));
                        order.setDiscountedPrice(orderDetail.getOrder().getOrder_detail().getPrice_info().getInteger("discounted_price"));

                        OrderVoDeliverInfoAddress addressInfo = orderDetail.getOrder().getOrder_detail().getDelivery_info().getAddress_info();
                        order.setUserName(addressInfo.getUser_name());
                        order.setPostalCode(addressInfo.getPostal_code());
                        order.setProvinceName(addressInfo.getProvince_name());
                        order.setCityName(addressInfo.getCity_name());
                        order.setCountyName(addressInfo.getCounty_name());
                        order.setDetailInfo(addressInfo.getDetail_info());
                        order.setTelNumber(addressInfo.getTel_number());
                        order.setHouseNumber(addressInfo.getHouse_number());
                        order.setVirtualOrderTelNumber(addressInfo.getVirtual_order_tel_number());
                        order.setTelNumberExtInfo(JSONObject.toJSONString(addressInfo.getTel_number_ext_info()));
                        order.setUseTelNumber(addressInfo.getUse_tel_number());
                        order.setHashCode(addressInfo.getHash_code());

                        order.setDeliveryProductInfo(JSONObject.toJSONString(orderDetail.getOrder().getOrder_detail().getDelivery_info().getDelivery_product_info()));

                        order.setShipDoneTime(orderDetail.getOrder().getOrder_detail().getDelivery_info().getShip_done_time());
                        order.setEwaybillOrderCode(orderDetail.getOrder().getOrder_detail().getDelivery_info().getEwaybill_order_code());

                        order.setSettleInfo(JSONObject.toJSONString(orderDetail.getOrder().getOrder_detail().getSettle_info()));

                        List<WeiOrderItem> itemList = new ArrayList<>();
                        for (var item:orderDetail.getOrder().getOrder_detail().getProduct_infos()) {
                            WeiOrderItem oi = new WeiOrderItem();
                            oi.setProductId(item.getProduct_id());
                            oi.setSkuId(item.getSku_id());
                            oi.setThumbImg(item.getThumb_img());
                            oi.setSkuCnt(item.getSku_cnt());
                            oi.setSalePrice(item.getSale_price());
                            oi.setTitle(item.getTitle());
                            oi.setOnAftersaleSkuCnt(item.getOn_aftersale_sku_cnt());
                            oi.setFinishAftersaleSkuCnt(item.getFinish_aftersale_sku_cnt());
                            oi.setSkuCode(item.getSku_code());
                            oi.setMarketPrice(item.getMarket_price());
                            oi.setRealPrice(item.getReal_price());
                            oi.setOutProductId(item.getOut_product_id());
                            oi.setOutSkuId(item.getOut_sku_id());
                            oi.setIsDiscounted(item.getIs_discounted() + "");
                            oi.setEstimatePrice(item.getEstimate_price());
                            oi.setIsChangePrice(item.getIs_change_price() + "");
                            oi.setChangePrice(item.getChange_price());
                            oi.setOutWarehouseId(item.getOut_warehouse_id());
                            oi.setUseDeduction(item.getUse_deduction() + "");

                            oi.setSkuAttrs(JSONObject.toJSONString(item.getSku_attrs()));
                            oi.setSkuDeliverInfo(JSONObject.toJSONString(item.getSku_deliver_info()));
                            oi.setExtraService(JSONObject.toJSONString(item.getExtra_service()));
                            oi.setOrderProductCouponInfoList(JSONObject.toJSONString(item.getOrder_product_coupon_info_list()));
                            itemList.add(oi);
                        }
                        order.setItems(itemList);
                        var result = weiOrderService.saveOrder(params.getShopId(),order);
                        if (result.getCode() == ResultVoEnum.DataExist.getIndex()) {
                            //已经存在
                            hasExistOrder++;
                        } else if (result.getCode() == ResultVoEnum.SUCCESS.getIndex()) {
                            insertSuccess++;
                        } else {
                            totalError++;
                        }
                    }
                }
            }

        }
        // 更新时间
        if(lasttime == null){
            // 新增
            SysShopPullLasttime insertLasttime = new SysShopPullLasttime();
            insertLasttime.setShopId(params.getShopId());
            insertLasttime.setCreateTime(new Date());
            insertLasttime.setLasttime(endTime);
            insertLasttime.setPullType("ORDER");
            pullLasttimeService.save(insertLasttime);

        }else {
            // 修改
            SysShopPullLasttime updateLasttime = new SysShopPullLasttime();
            updateLasttime.setId(lasttime.getId());
            updateLasttime.setUpdateTime(new Date());
            updateLasttime.setLasttime(endTime);
            pullLasttimeService.updateById(updateLasttime);
        }

        SysShopPullLogs logs = new SysShopPullLogs();
        logs.setShopType(EnumShopType.WEI.getIndex());
        logs.setShopId(params.getShopId());
        logs.setPullType("ORDER");
        logs.setPullWay("主动拉取");
        logs.setPullParams("{startTime:"+startTime+",endTime:"+endTime+"}");
        logs.setPullResult("{insert:"+insertSuccess+",update:"+hasExistOrder+",fail:"+totalError+"}");
        logs.setPullTime(currDateTime);
        logs.setDuration(System.currentTimeMillis() - beginTime);
        pullLogsService.save(logs);

        return AjaxResult.success();
    }

    /**
     * 拉取订单详情
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/pull_detail", method = RequestMethod.POST)
    public AjaxResult pullDetail(@RequestBody PullRequest params) throws Exception {
        if (params.getShopId() == null || params.getShopId() <= 0) {
            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "参数错误，没有店铺Id");
        }
        if (StringUtils.isEmpty(params.getOrderId())) {
            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "参数错误，没有订单编号");
        }

        Date currDateTime = new Date();
        long beginTime = System.currentTimeMillis();

        var checkResult = apiCommon.checkBefore(params.getShopId());
        if (checkResult.getCode() != ResultVoEnum.SUCCESS.getIndex()) {
            return AjaxResult.error(checkResult.getCode(), checkResult.getMsg(), checkResult.getData());
        }
        String accessToken = checkResult.getData().getAccessToken();
        String serverUrl = checkResult.getData().getServerUrl();
        String appKey = checkResult.getData().getAppKey();
        String appSecret = checkResult.getData().getAppSecret();

        OrderApiService remoting = RemoteUtil.Remoting(serverUrl, OrderApiService.class);
        OrderDetailBo bo = new OrderDetailBo();
        bo.setOrder_id(params.getOrderId());
        OrderDetailVo orderDetail = remoting.getOrderDetail(accessToken, bo);
        if (orderDetail.getErrcode() == 0) {
            WeiOrder order = new WeiOrder();
            order.setOrderId(orderDetail.getOrder().getOrder_id());
            order.setShopId(params.getShopId());
            order.setOpenid(orderDetail.getOrder().getOpenid());
            order.setCreateTime(orderDetail.getOrder().getCreate_time());
            order.setUpdateTime(orderDetail.getOrder().getUpdate_time());
            order.setUnionid(orderDetail.getOrder().getUnionid());
            order.setStatus(orderDetail.getOrder().getStatus());
            order.setAftersaleDetail(JSONObject.toJSONString(orderDetail.getOrder().getAftersale_detail()));
            order.setPayInfo(JSONObject.toJSONString(orderDetail.getOrder().getOrder_detail().getPay_info()));
            order.setProductPrice(orderDetail.getOrder().getOrder_detail().getPrice_info().getInteger("product_price"));
            order.setOrderPrice(orderDetail.getOrder().getOrder_detail().getPrice_info().getInteger("order_price"));
            order.setFreight(orderDetail.getOrder().getOrder_detail().getPrice_info().getInteger("freight"));
            order.setDiscountedPrice(orderDetail.getOrder().getOrder_detail().getPrice_info().getInteger("discounted_price"));

            OrderVoDeliverInfoAddress addressInfo = orderDetail.getOrder().getOrder_detail().getDelivery_info().getAddress_info();
            order.setUserName(addressInfo.getUser_name());
            order.setPostalCode(addressInfo.getPostal_code());
            order.setProvinceName(addressInfo.getProvince_name());
            order.setCityName(addressInfo.getCity_name());
            order.setCountyName(addressInfo.getCounty_name());
            order.setDetailInfo(addressInfo.getDetail_info());
            order.setTelNumber(addressInfo.getTel_number());
            order.setHouseNumber(addressInfo.getHouse_number());
            order.setVirtualOrderTelNumber(addressInfo.getVirtual_order_tel_number());
            order.setTelNumberExtInfo(JSONObject.toJSONString(addressInfo.getTel_number_ext_info()));
            order.setUseTelNumber(addressInfo.getUse_tel_number());
            order.setHashCode(addressInfo.getHash_code());

            order.setDeliveryProductInfo(JSONObject.toJSONString(orderDetail.getOrder().getOrder_detail().getDelivery_info().getDelivery_product_info()));

            order.setShipDoneTime(orderDetail.getOrder().getOrder_detail().getDelivery_info().getShip_done_time());
            order.setEwaybillOrderCode(orderDetail.getOrder().getOrder_detail().getDelivery_info().getEwaybill_order_code());

            order.setSettleInfo(JSONObject.toJSONString(orderDetail.getOrder().getOrder_detail().getSettle_info()));

            List<WeiOrderItem> itemList = new ArrayList<>();
            for (var item : orderDetail.getOrder().getOrder_detail().getProduct_infos()) {
                WeiOrderItem oi = new WeiOrderItem();
                oi.setProductId(item.getProduct_id());
                oi.setSkuId(item.getSku_id());
                oi.setThumbImg(item.getThumb_img());
                oi.setSkuCnt(item.getSku_cnt());
                oi.setSalePrice(item.getSale_price());
                oi.setTitle(item.getTitle());
                oi.setOnAftersaleSkuCnt(item.getOn_aftersale_sku_cnt());
                oi.setFinishAftersaleSkuCnt(item.getFinish_aftersale_sku_cnt());
                oi.setSkuCode(item.getSku_code());
                oi.setMarketPrice(item.getMarket_price());
                oi.setRealPrice(item.getReal_price());
                oi.setOutProductId(item.getOut_product_id());
                oi.setOutSkuId(item.getOut_sku_id());
                oi.setIsDiscounted(item.getIs_discounted() + "");
                oi.setEstimatePrice(item.getEstimate_price());
                oi.setIsChangePrice(item.getIs_change_price() + "");
                oi.setChangePrice(item.getChange_price());
                oi.setOutWarehouseId(item.getOut_warehouse_id());
                oi.setUseDeduction(item.getUse_deduction() + "");

                oi.setSkuAttrs(JSONObject.toJSONString(item.getSku_attrs()));
                oi.setSkuDeliverInfo(JSONObject.toJSONString(item.getSku_deliver_info()));
                oi.setExtraService(JSONObject.toJSONString(item.getExtra_service()));
                oi.setOrderProductCouponInfoList(JSONObject.toJSONString(item.getOrder_product_coupon_info_list()));
                itemList.add(oi);
            }
            order.setItems(itemList);
            weiOrderService.saveOrder(params.getShopId(), order);
        }

        return AjaxResult.success();
    }
}


