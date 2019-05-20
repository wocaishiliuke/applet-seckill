/**
 *  模块化javaScript
 */
var seckill = {
    // 封装秒杀相关的ajax的url
    URL: {
        now: function () {
            return "/skProduct/now";
        },
        expose: function (id) {
            return "/skProduct/" + "expose/" + id;
        },
        seckill: function (id, md5) {
            return "/skProduct/" + "seckill/" + id + "/" + md5;
        }
    },

    // 验证手机号码
    validatePhone: function (phone) {
        return !!(phone && phone.length === 11 && !isNaN(phone));
    },

    // 详情页秒杀业务逻辑
    detail: {
        // 详情页初始化
        init: function (params) {
            // 手机号验证（获取Cookie，无则返回undefined）
            var userPhone = $.cookie('userPhone');
            // 未填写手机号码时
            if (!seckill.validatePhone(userPhone)) {
                // 弹出手机输入对话框
                var killPhoneModal = $("#killPhoneModal");
                killPhoneModal.modal({
                    show: true,         // 显示弹出层
                    backdrop: 'static', // 静止位置关闭
                    keyboard: false     // 关闭键盘事件
                });
                // 点击提交手机号
                $("#killPhoneBtn").click(function () {
                    var inputPhone = $("#killPhoneKey").val();
                    console.log("inputPhone" + inputPhone);
                    if (seckill.validatePhone(inputPhone)) {
                        // 写入cookie
                        $.cookie('userPhone', inputPhone, {expires: 7, path: '/skProduct'});
                        // 刷新页面
                        window.location.reload();
                    } else {
                        $("#killPhoneMessage").hide().html("<label class='label label-danger'>手机号码错误</label>").show(300);
                    }
                });
            // 填写过手机号码时（cookie中有手机号）
            } else {
                var startTime = params['startTime'];
                var endTime = params['endTime'];
                var id = params['id'];
                console.log("开始秒杀时间=======" + startTime);
                console.log("结束秒杀时间========" + endTime);
                // 获取服务器当前时间
                $.get(seckill.URL.now(), {}, function (result) {
                    if (result && result['success']) {
                        var nowTime = seckill.convertTime(result['data']);
                        console.log("服务器当前的时间==========" + nowTime);
                        // 秒杀时间判断、倒计时
                        seckill.countDown(id, nowTime, startTime, endTime);
                    } else {
                        console.log('结果:' + result);
                    }
                });
            }

        }
    },

    /**
     * 获取该商品秒杀地址，进行秒杀；未暴露地址时，进行倒计时
     * id
     * mode 秒杀按钮
     */
    handlerSeckill: function (id, mode) {
        mode.hide().html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        // 获取秒杀地址
        $.get(seckill.URL.expose(id), {}, function (result) {
            if (result && result['success']) {
                var exposer = result['data'];
                if (exposer['exposed']) {
                    // 该商品已暴露秒杀地址, 拼接秒杀接口地址
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.seckill(id, md5);
                    console.log("秒杀的地址为:" + killUrl);
                    // 显示秒杀按钮
                    mode.show();
                    // 绑定一次点击事件
                    $("#killBtn").one('click', function () {
                        // 开始执行秒杀请求，禁用按钮
                        $(this).addClass("disabled");
                        // 发送秒杀请求
                        $.post(killUrl, {}, function (result) {
                            var killResult = result['data'];
                            var state = killResult['state'];
                            var stateInfo = killResult['stateInfo'];
                            // 显示秒杀结果
                            mode.html('<span class="label label-success">' + stateInfo + '</span>');
                        });

                    });
                } else {
                    // 该商品还未暴露秒杀地址, 进行倒计时
                    var now = seckill.convertTime(exposer['now']);
                    var start = seckill.convertTime(exposer['start']);
                    var end = seckill.convertTime(exposer['end']);
                    console.log("开始倒计时");
                    seckill.countDown(id, now, start, end);
                }
            } else {
                console.error("服务器查询秒杀商品详情失败");
                console.log('result' + result.valueOf());
            }
        });
    },

    /**
     * 秒杀时间处理（已开始、已结束、未开始的倒计时）
     * id 秒杀商品ID
     * nowTime 服务器返回的当前时间
     * startTime 秒杀开始时间
     * endTime
     */
    countDown: function (id, nowTime, startTime, endTime) {
        // 获取显示倒计时的文本域
        var seckillBox = $("#seckill-box");
        // 转换成时间戳进行比较
        nowTime = new Date(nowTime).valueOf();
        startTime = new Date(startTime).valueOf();
        endTime = new Date(endTime).valueOf();

        if (nowTime < endTime && nowTime > startTime) {
            // 秒杀已开始
            seckill.handlerSeckill(id, seckillBox);
        }
        else if (nowTime > endTime) {
            // 秒杀已结束
            seckillBox.html("秒杀结束");
        } else {
            // 秒杀未开启
            var killTime = new Date(startTime + 1000);
            console.log(killTime);
            console.log("开始计时效果");
            seckillBox.countdown(killTime, function (event) {
                // 事件格式
                var format = event.strftime("秒杀倒计时: %D天 %H时 %M分 %S秒");
                console.log(format);
                seckillBox.html(format);
            }).on('finish.countdown', function () {
                // 事件完成后回调事件
                console.log("倒计时结束, 准备回调: 获取秒杀地址, 执行秒杀");
                seckill.handlerSeckill(id, seckillBox);
            });
        }
    },

    cloneZero: function (time) {
        var cloneZero = ":00";
        if (time.length < 6) {
            console.warn("需要拼接时间");
            time = time + cloneZero;
            return time;
        } else {
            console.log("时间是完整的");
            return time;
        }
    },

    convertTime: function (localDateTime) {
        var year = localDateTime.year;
        var monthValue = localDateTime.monthValue;
        var dayOfMonth = localDateTime.dayOfMonth;
        var hour = localDateTime.hour;
        var minute = localDateTime.minute;
        var second = localDateTime.second;
        return year + "-" + monthValue + "-" + dayOfMonth + " " + hour + ":" + minute + ":" + second;
    }
};