package tech.qihangec.api.controller;


import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.qihangec.api.common.*;
import tech.qihangec.api.domain.ErpGoods;
import tech.qihangec.api.domain.ErpGoodsSku;
import tech.qihangec.api.service.ErpGoodsService;
import tech.qihangec.api.service.ErpGoodsSkuService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品管理Controller
 * 
 * @author qihang
 * @date 2023-12-29
 */
@AllArgsConstructor
@RestController
@RequestMapping("/goods")
public class GoodsController extends BaseController
{
    private final ErpGoodsService goodsService;
    private final ErpGoodsSkuService skuService;

    /**
     * 搜索商品SKU
     * 条件：商品编码、SKU、商品名称
     */
//    @GetMapping("/searchSku")
//    public TableDataInfo searchSkuBy(String keyword)
//    {
//        List<GoodsSpecListVo> list = goodsService.searchGoodsSpec(keyword);
//        return getDataTable(list);
//    }
//
    @GetMapping("/sku_list")
    public TableDataInfo skuList(ErpGoodsSku bo, PageQuery pageQuery)
    {
        var pageList = goodsService.querySkuPageList(bo,pageQuery);
        return getDataTable(pageList);
    }

    /**
     * 查询商品管理列表
     */
    @PreAuthorize("@ss.hasPermi('goods:goods:list')")
    @GetMapping("/list")
    public TableDataInfo list(ErpGoods goods, PageQuery pageQuery)
    {
        PageResult<ErpGoods> pageResult = goodsService.queryPageList(goods, pageQuery);
        return getDataTable(pageResult);
    }
//
//    /**
//     * 获取商品管理详细信息
//     */
//    @PreAuthorize("@ss.hasPermi('goods:goods:query')")
//    @GetMapping(value = "/{id}")
//    public AjaxResult getInfo(@PathVariable("id") Long id)
//    {
//        return success(goodsService.selectGoodsById(id));
//    }
//    /**
//     * 获取商品管理详细信息
//     */
//    @PreAuthorize("@ss.hasPermi('goods:goods:query')")
//    @GetMapping(value = "/sku/{id}")
//    public AjaxResult getSkuInfo(@PathVariable("id") Long id)
//    {
//        return success(skuService.getById(id));
//    }
    /**
     * 新增商品管理
     */
    @PostMapping("/add")
    public AjaxResult add(@RequestBody ErpGoods goods)
    {
        goods.setCreateBy(getUsername());
        int result = goodsService.insertGoods(goods);
        if(result == -1) new AjaxResult(501,"商品编码已存在");
        return toAjax(1);
    }
//
//    @PreAuthorize("@ss.hasPermi('goods:goods:add')")
//    @PostMapping("/goodsSku")
//    public AjaxResult addSku(@RequestBody OGoodsSku goodsSku)
//    {
//
//        int result = goodsService.insertGoodsSku(goodsSku);
//        if(result == -1) new AjaxResult(501,"商品编码已存在");
//        return toAjax(1);
//    }
//
//    /**
//     * 修改商品管理
//     */
//    @PreAuthorize("@ss.hasPermi('goods:goods:edit')")
//    @PutMapping
//    public AjaxResult edit(@RequestBody OGoods goods)
//    {
//        return toAjax(goodsService.updateGoods(goods));
//    }
//    @PutMapping("/sku")
//    public AjaxResult editSku(@RequestBody OGoodsSku sku)
//    {
//        return toAjax(skuService.updateById(sku));
//    }
//
//    /**
//     * 删除商品管理
//     */
//    @PreAuthorize("@ss.hasPermi('goods:goods:remove')")
//    @DeleteMapping("/{ids}")
//    public AjaxResult remove(@PathVariable Long[] ids)
//    {
//        return toAjax(goodsService.deleteGoodsByIds(ids));
//    }
//
//    @RequestMapping(value = "/goods_sku_import", method = RequestMethod.POST)
//    public AjaxResult orderSendExcel(@RequestPart("file") MultipartFile file) throws IOException, InvalidFormatException {
//
//        String fileName = file.getOriginalFilename();
//        String dir = System.getProperty("user.dir");
//        String destFileName = dir + File.separator + "/import/uploadedfiles_" + fileName;
//        System.out.println(destFileName);
//        File destFile = new File(destFileName);
//        file.transferTo(destFile);
//        InputStream fis = null;
//        fis = new FileInputStream(destFileName);
//        if (fis == null) return AjaxResult.error("没有文件");
//
//        Workbook workbook = null;
//
//        try {
//            if (fileName.toLowerCase().endsWith("xlsx")) {
//                workbook = new XSSFWorkbook(fis);
//            } else if (fileName.toLowerCase().endsWith("xls")) {
//                workbook = new HSSFWorkbook(fis);
//            }
//            // workbook = new HSSFWorkbook(fis);
//        } catch (Exception ex) {
//            return AjaxResult.error(ex.getMessage());
//        }
//
//        if (workbook == null) return AjaxResult.error(502, "未读取到Excel文件");
//
//        /****************开始处理excel****************/
//        int success = 0;
//        int fail = 0;
//        Sheet sheet = null;
//        try {
//            sheet = workbook.getSheetAt(0);
//            int lastRowNum = sheet.getLastRowNum();//最后一行索引
//            Row row = null;
//
//            for (int i = 1; i <= lastRowNum; i++) {
//                row = sheet.getRow(i);
//                //数据
//                OGoodsSku  sku = new OGoodsSku();
//                for(int c=0;c<6;c++){
//                    Cell cell = row.getCell(c);
//                    String cellValue = "";
//                    if (cell != null) {
//                        if (cell.getCellType() == CellType.STRING) {
//                            cellValue = cell.getStringCellValue().replace("\t", "");
//                        } else if (cell.getCellType() == CellType.NUMERIC) {
//                            cellValue = NumberToTextConverter.toText(cell.getNumericCellValue()).replace("\t", "");
//                        }
//                    }
//                    if(c == 1) {
//                        if(StringUtils.hasText(cellValue) ){
//                            sku.setErpGoodsId(Long.parseLong(cellValue));
//                        }else {
//                            sku.setErpGoodsId(0L);
//                        }
//                    }
//                    if(StringUtils.hasText(cellValue)) {
//                        if (c == 0) {
//                            sku.setErpSkuId(Long.parseLong(cellValue));
//                        } else if (c == 2) {
//                            sku.setSkuNum(cellValue);
//                        } else if (c == 3) {
//                            sku.setSkuName(cellValue);
//                        } else if (c == 4) {
//                            sku.setColorImage(cellValue);
//                        } else if (c == 5) {
//                            sku.setRemark(cellValue);
//                        }
//                    }
//                }
//                goodsService.insertGoodsSku(sku);
//                success++;
//            }
//
//
//        } catch (Exception ex) {
//           fail++;
//            ex.printStackTrace();
//        }
//        Map<String, Integer> result = new HashMap<>();
//        result.put("success",success);
//        result.put("fail",fail);
//        return AjaxResult.success(result);
//    }
}
