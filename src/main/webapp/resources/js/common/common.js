// img 是验证码控件
function changeVerifyCode(img) {
    // 提交至 servlet，生成新的验证码
    img.src = "../Kaptcha？" + Math.floor(Math.random() * 100);
}