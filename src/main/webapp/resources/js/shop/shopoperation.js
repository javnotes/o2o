// $(function () {} 为初始化函数
$(function () {

    // 对应后台方法@RequestMapping
    var initUrl = '/o2o/shopadmin/getshopinitinfo';
    var registerShopUrl = '/o2o/shopadmin/registershop';

    // 加载到这时，调用方法 getShopInitInfo
    getShopInitInfo();

    // 方法1：从后台获取内容并添加至 html 中
    function getShopInitInfo() {
        // $.getJSON() 方法使用 AJAX 的 HTTP GET 请求获取 JSON 数据。
        $.getJSON(initUrl, function (data) {
            if (data.success) {
                var tempHtml = "";
                var temmpAreaHtml = "";
                data.shopCategoryList.map(function (item, index) {
                    tempHtml += '<option data-id="' + item.shopCataegoryid
                        + '">' + item.shopCataegoryName + '</option>>';
                });
                data.areaList.map(function (item, index) {
                    tempAreaHtml += '<option data-id="' + item.areaId + '">'
                        + item.areaName + '</option>';
                });
                // 将上面获取的内容，根据 html 中的 id，添加进去。
                $('#shop-capegory').html(tempHtml);
                $('#area').html(temmpAreaHtml);
            }
        });
        // 方法2：点击按钮后，获取表单的内容
        $('#submit').click(function () {
            // 一个 json 对象
            var shop = {};
            shop.shopName = $('#shop-name').val;
            shop.shopAddr = $('#shop-addr').val;
            shop.phone = $('#shop-phone').val;
            shop.shopDesc = $('#shop-desc').val;
            // 从列表中获取 shopCategoryId
            // jQuery 元素选择器：$("p#demo") 选取所有 id="demo" 的 <p> 元素
            shop.shopCategory = {
                shopCategoryId: $('#shop-category').find('option').not(function () {
                    return !this.selected;
                }).data('id')
            };

            shop.area = {
                areaId: $('#area').find('option').not(function () {
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
                url: registerShopUrl,
                type: 'POST',
                data: formData,
                contentType: false,
                processData: false,
                cache: false,
                // data 为后台返回的 jSon 数据
                // 后台方法均定义了 success 的 key，是用在这里的
                success: function (data) {
                    if (data.success) {
                        $.toast("提交成功～");
                    } else {
                        $.toast("提交失败 " + data.errorMessage);
                    }
                    $('#kaptcha_img').click();
                }
            });
        });
    }
})
// 初始化 调用
// js 文件被加载时，就调用 js 中的方法
// html 中要引入 js 文件
// 在 html 文件中，先引入 css，最后引入 js 的目的是，希望加载页面时，先渲染出布局，再填充内容(js)