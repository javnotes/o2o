// $(function () {} 为初始化函数
$(function () {

    // 传入shopId，则更新这个店铺，否则是新增
    var shopId = getQueryString('shopId');
    var isEdit = shopId ? true : false;

    // 对应后台方法@RequestMapping
    var initUrl = '/o2o/shopadmin/getshopinitinfo';
    var registerShopUrl = '/o2o/shopadmin/registershop';
    var shopInfoUrl = '/o2o/shopadmin/getshopbyid?shopId=' + shopId;
    var editShopUrl = '/o2o/shopadmin/modifyshop';

    if (isEdit) {
        getShopInfo(shopId);
    } else {
        // 加载到这时，调用方法 getShopInitInfo
        getShopInitInfo();
    }

    // 方法3：通过店铺Id获取店铺信息
    function getShopInfo(shopId) {
        $.getJSON(shopInfoUrl, function (data) {
            if (data.success) {
                // 若访问成功，则依据后台传递过来的店铺信息为表单元素赋值
                var shop = data.shop;
                $('#shop-name').val(shop.shopName);
                $('#shop-addr').val(shop.shopAddr);
                $('#shop-phone').val(shop.phone);
                $('#shop-desc').val(shop.shopDesc);
                // 给店铺类别选定原先的店铺类别值
                var shopCategory = '<option data-id="'
                    + shop.shopCategory.shopCategoryId + '" selected>'
                    + shop.shopCategory.shopCategoryName + '</option>';
                var tempAreaHtml = '';
                // 初始化区域列表
                data.areaList.map(function (item, index) {
                    tempAreaHtml += '<option data-id="' + item.areaId + '">'
                        + item.areaName + '</option>';
                });
                $('#shop-category').html(shopCategory);
                // 不允许选择店铺类别
                $('#shop-category').attr('disabled', 'disabled');
                $('#area').html(tempAreaHtml);
                // 给店铺选定原先的所属的区域
                $("#area option[data-id='" + shop.area.areaId + "']").attr(
                    "selected", "selected");
            }
        });
    }

    // 方法1：从后台获取内容并添加至 html 中，获取店铺列表信息
    function getShopInitInfo() {
        // $.getJSON() 方法使用 AJAX 的 HTTP GET 请求获取 JSON 数据。
        // 获取信息的方法，返回的形式是 json 字符串，initUrl：需访问的 url，function (data) {}：回调方法
        $.getJSON(initUrl, function (data) {
            if (data.success) {
                var tempHtml = '';
                var tempAreaHtml = '';
                data.shopCategoryList.map(function (item, index) {
                    tempHtml += '<option data-id="' + item.shopCategoryId + '">'
                        + item.shopCategoryName + '</option>';
                });
                data.areaList.map(function (item, index) {
                    tempAreaHtml += '<option data-id="' + item.areaId + '">'
                        + item.areaName + '</option>';
                });
                // 将上面获取的内容，根据 html 中的 id，添加进去。
                $('#shop-category').html(tempHtml);
                $('#area').html(tempAreaHtml);
            }
        });
    }

    // 方法2：点击按钮后，获取表单的内容
    $('#submit').click(function () {
        // 一个 json 对象
        var shop = {};

        // 编辑时，要传入 shopId
        if (isEdit) {
            shop.shopId = shopId;
        }

        shop.shopName = $('#shop-name').val();
        shop.shopAddr = $('#shop-addr').val();
        shop.phone = $('#shop-phone').val();
        shop.shopDesc = $('#shop-desc').val();
        // 从列表中获取 shopCategoryId
        // jQuery 元素选择器：$("p#demo") 选取所有 id="demo" 的 <p> 元素
        // 选择选定好的区域信息
        shop.area = {
            areaId: $('#area').find('option').not(function () {
                return !this.selected;
            }).data('id')
        };

        // 选择选定好的店铺类别
        shop.shopCategory = {
            shopCategoryId: $('#shop-category').find('option').not(function () {
                return !this.selected;
            }).data('id')
        };

        // 上传的文件流
        var shopImg = $('#shop-img')[0].files[0];

        var formData = new FormData();
        // 加入 文件流(var shopImg)，表单内容(var shop)-先转换为字符流
        // JSON.stringify() 方法用于将 JavaScript 值转换为 JSON 字符串。
        formData.append('shopStr', JSON.stringify(shop));
        formData.append('shopImg', shopImg);

        // 验证码
        var actualVerifyCode = $('#in_kaptcha').val();
        if (!actualVerifyCode) {
            $.toast('请输入验证码');
            return;
        }
        formData.append('actualVerifyCode', actualVerifyCode);

        // 使用 ajax 提交至后台
        $.ajax({
            url: (isEdit ? editShopUrl : registerShopUrl),
            type: 'POST',
            data: formData,
            contentType: false,
            processData: false,
            cache: false,
            // data 为后台返回的 jSon 数据
            // 后台方法均定义了 success 的 key，是用在这里的
            success: function (data) {
                if (data.success) {
                    $.toast('提交成功～');
                } else {
                    $.toast('提交失败 ' + data.errorMessage);
                }
                // alert(data);
                $('#kaptcha_img').click();
            },
        });
    });
})
// 初始化 调用
// js 文件被加载时，就调用 js 中的方法
// html 中要引入 js 文件
// 在 html 文件中，先引入 css，最后引入 js 的目的是，希望加载页面时，先渲染出布局，再填充内容(js)


