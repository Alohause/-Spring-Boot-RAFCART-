(function ($) {
    "use strict";

    // hide perloader
    window.onload = function () {
        $('.preloader').fadeOut(500, function(){ $('.preloader').remove(); } );
    }

    // Mobile menu
    $('#mob_menubar').on('click', function () {
        $('#mob_menu').toggleClass('active')
    })

    // product filter in mobile
    $('#mobile_filter_btn').on('click', function () {
        $('.filter_box').toggleClass('active')
    })

    $('.close_filter').on('click', function () {
        $('.filter_box').removeClass('active')
    })

    // search for mobile
    $('#src_icon').on('click', function () {
        $('.mobile_search_bar').addClass('active')
    })

    $('#close_mbsearch').on('click', function () {
        $('.mobile_search_bar').removeClass('active')
    })

    // payment method switch
    $('.single_payment_method').on('click', function () {
        let getCls = $(this).attr('data-target')
        $('.single_payment_method, .payment_methods').removeClass('active')
        $(getCls).addClass('active')
        $(this).addClass('active')
    })

    // nice selector
    $('.nice_select').niceSelect();

    // banner slider
    $('.banner_slider').slick({
        slidesToShow: 1,
        slidesToScroll: 1,
        arrows: true,
        dots: true,
        prevArrow: '<button type="button" class="slick-prev"><i class="las la-angle-left"></i></button>',
        nextArrow: '<button type="button" class="slick-next"><i class="las la-angle-right"></i></button>',
        responsive: [{
            breakpoint: 1300,
            settings: {
                arrows: false,
            }
        }]
    });

    // Hero slider
    $('.hero_slider_active').slick({
        slidesToShow: 1,
        slidesToScroll: 1,
        arrows: false,
        dots: true
    });

    // single product view slider
    $('.product_view_slider').slick({
        slidesToShow: 1,
        slidesToScroll: 1,
        arrows: false,
        fade: true,
        asNavFor: '.product_viewslid_nav',
        infinite: false
    });

    // single product view slider nav
    $('.product_viewslid_nav').slick({
        slidesToShow: 5,
        slidesToScroll: 1,
        prevArrow: '<button type="button" class="slick-prev"><i class="las la-angle-left"></i></button>',
        nextArrow: '<button type="button" class="slick-next"><i class="las la-angle-right"></i></button>',
        asNavFor: '.product_view_slider',
        focusOnSelect: true,
        centerMode: false,
        centerPadding: '0px',
        infinite: false,
        responsive: [{
            breakpoint: 576,
            settings: {
                slidesToShow: 3,
            }
        }]
    });

    // product slider
    $('.product_slider_2').slick({
        dots: false,
        arrows: true,
        infinite: true,
        prevArrow: '<button type="button" class="slick-prev"><i class="las la-angle-left"></i></button>',
        nextArrow: '<button type="button" class="slick-next"><i class="las la-angle-right"></i></button>',
        speed: 300,
        slidesToShow: 4,
        slidesToScroll: 1,
        responsive: [
            {
                breakpoint: 1366,
                settings: {
                    arrows: false,
                }
            },{
                breakpoint: 1200,
                settings: {
                    slidesToShow: 3,
                    arrows: false,
                }
            },
            {
                breakpoint: 992,
                settings: {
                    slidesToShow: 2,
                    arrows: false,
                }
            },
            {
                breakpoint: 480,
                settings: {
                    slidesToShow: 1,
                    arrows: false,
                }
            }
        ]
    });

    // team slider
    $('.team_slider').slick({
        dots: false,
        arrows: false,
        infinite: true,
        speed: 300,
        slidesToShow: 4,
        slidesToScroll: 1,
        responsive: [
            {
                breakpoint: 1366,
                settings: {
                    arrows: false,
                }
            },{
                breakpoint: 1200,
                settings: {
                    slidesToShow: 3,
                    arrows: false,
                }
            },
            {
                breakpoint: 992,
                settings: {
                    slidesToShow: 2,
                    arrows: false,
                }
            },
            {
                breakpoint: 480,
                settings: {
                    slidesToShow: 1,
                    arrows: false,
                }
            }
        ]
    });

    // brand slider
    $('.brand_slider').slick({
        dots: false,
        arrows: false,
        infinite: true,
        speed: 300,
        slidesToShow: 6,
        slidesToScroll: 1,
        responsive: [
            {
                breakpoint: 1366,
                settings: {
                    arrows: false,
                }
            },{
                breakpoint: 1200,
                settings: {
                    slidesToShow: 5,
                    arrows: false,
                }
            },
            {
                breakpoint: 992,
                settings: {
                    slidesToShow: 4,
                    arrows: false,
                }
            },
            {
                breakpoint: 768,
                settings: {
                    slidesToShow: 3,
                    arrows: false,
                }
            },
            {
                breakpoint: 576,
                settings: {
                    slidesToShow: 2,
                    arrows: false,
                }
            }
        ]
    });

    // search suggest
    $('#show_suggest').on('focus',function(){
        $('.search_suggest').addClass('active')
    })
    $('#show_suggest').on('focusout',function(){
        $('.search_suggest').removeClass('active')
    })

    
    // switch product bottom section
    $('.pbt_single_btn').on('click', function () {
        let getCls = $(this).attr('data-target')
        $('.pb_tab_content, .pbt_single_btn').removeClass('active')
        $(getCls).addClass('active')
        $(this).addClass('active')
    })

    // Price Range slider
    $(function () {
        $("#slider-range").slider({
            range: true,
            min: 1,
            max: 1000,
            values: [150, 500],
            slide: function (event, ui) {
                $("#amount").val("$" + ui.values[0] + " - $" + ui.values[1]);
            }
        });
        $("#amount").val("$" + $("#slider-range").slider("values", 0) +
            " - $" + $("#slider-range").slider("values", 1));
    });

    // Mobile categories
    $('.singlecats.withsub span').click(function () {
        if ($(this).closest('.singlecats').hasClass('active')) {
            $(this).closest('.singlecats').removeClass('active')
            $('.mega_menu_wrap').removeClass('active')
        } else {
            $('.singlecats').removeClass('active')
            $(this).closest('.singlecats').addClass('active')
        }
    })

    $('.mega_menu_wrap h4').click(function () {
        if ($(this).closest('.mega_menu_wrap').hasClass('active')) {
            $(this).closest('.mega_menu_wrap').removeClass('active')
        } else {
            $('.mega_menu_wrap').removeClass('active')
            $(this).closest('.mega_menu_wrap').addClass('active')
        }
    })

    $('.all_category .bars, .open_category').click(function () {
        $('#mobile_catwrap').addClass('active')
    })
        
    $('#catclose').click(function () {
        $('#mobile_catwrap').removeClass('active')
    })

    // Menu
    $('.open_menu').click(function () {
        $('#mobile_menwrap').addClass('active')
    })

    $('#menuclose').click(function () {
        $('#mobile_menwrap').removeClass('active')
    })

    // mobile cart
    $('#openCart').click(function () {
        $('#mobileCart').addClass('active')
    })

    $('#mobileCartClose').click(function () {
        $('#mobileCart').removeClass('active')
    })

    // outside click handle
    $(document).on('click', function(e){
        if(e.target.id==='mobile_menwrap'){
            $('#mobile_menwrap').removeClass('active')
        }
        if(e.target.id==='mobile_catwrap'){
            $('#mobile_catwrap').removeClass('active')
            $('.singlecats').removeClass('active')
            $('.mega_menu_wrap').removeClass('active')
        }
        if(e.target.classList.contains('product_quickview')){
            $('.product_quickview').removeClass('active');
            $('body').css('overflow-y', 'auto')
        }
        if(e.target.classList.contains('popup_wrap')){
            $('.popup_wrap').removeClass('active');
            $('body').css('overflow-y', 'auto')
        }
        if(e.target.id==='mobileCart'){
            $('#mobileCart').removeClass('active');
        }

        $('.acprof_wrap').removeClass('active')
    })

    $('.profile_hambarg').on('click', function(e){
        e.stopPropagation();
        $('.acprof_wrap').toggleClass('active')
    })

    $('.acprof_wrap').on('click', function(e){
        e.stopPropagation();
    })

    // product quick view
    $('.open_quickview').on('click', function(){
        $('.product_quickview').addClass('active');
        $('body').css('overflow-y', 'hidden')
    })

    $('.close_quickview').on('click', function(){
        $('.product_quickview').removeClass('active');
        $('body').css('overflow-y', 'auto')
    })

    // mobile submenu
    $('.mobile_menu_2 .withsub').on('click', function(){
        if($(this).hasClass('active')){
            $('.mobile_menu_2 .withsub').removeClass('active')
        }else{
            $('.mobile_menu_2 .withsub').removeClass('active')
            $(this).addClass('active')
        }
    })

    // popup show
    //setTimeout(function(){
    //    $('.popup_wrap').addClass('active')
    //}, 2000)
    
    $('.close_popup').on('click', function(){
        $('.popup_wrap').removeClass('active')
    })

    // timer
    //count down
    function startTimer(duration) {
        var timer = duration, minutes, seconds;
        setInterval(function () {
            minutes = parseInt(timer / 60, 10)
            seconds = parseInt(timer % 60, 10);

            minutes = minutes < 10 ? "0" + minutes : minutes;
            seconds = seconds < 10 ? "0" + seconds : seconds;

            $('#count_minute').text(minutes)
            $('#count_second').text(seconds)

            if (--timer < 0) {
                timer = duration;
            }

        }, 1000);
    }

    startTimer(2000)

    // activate bootstrap tooltip
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
    return new bootstrap.Tooltip(tooltipTriggerEl)
    })

    $(document).ready(function() {
        checkLoginState();
    });

    function checkLoginState() {
        var userStr = localStorage.getItem('currentUser');
        
        if (userStr) {
            try {
                var user = JSON.parse(userStr);
                
                var $topAuthArea = $('.tophead_items .me-3'); 
                if ($topAuthArea.length > 0) {
                    $topAuthArea.html(`
                        <a class="text-semibold me-0" href="account.html">
                            <i class="las la-user"></i> ${user.name}
                        </a>
                        <span class="text_xs">/</span>
                        <a class="text-semibold" href="javascript:void(0)" onclick="handleLogout()">Logout</a>
                    `);
                }

                var $dropdownArea = $('.ac_join');
                
                if ($dropdownArea.length > 0) {
                    $dropdownArea.html(`
                        <p style="margin-bottom: 10px; color: #333;">Hello, <strong>${user.name}</strong></p>
                        <div class="account_btn d-flex justify-content-center">
                           <a href="javascript:void(0)" onclick="handleLogout()" class="default_btn w-100" style="padding: 8px 0;">Logout</a>
                        </div>
                    `);
                }

                $('.ac_links a').each(function() {
                    if ($(this).text().trim().toLowerCase().includes('log out')) {
                        $(this).attr('href', 'javascript:void(0)');
                        $(this).attr('onclick', 'handleLogout()');
                    }
                });

            } catch (e) {
                console.error("解析用户信息失败", e);
                localStorage.removeItem('currentUser'); 
            }
        }
    }

    window.handleLogout = function() {
        fetchJson('/users/logout', { method: 'POST' }).finally(() => {
            localStorage.removeItem('currentUser');
            localStorage.removeItem('userId'); 
            window.location.href = 'login.html';
        });
    }
})(jQuery);

async function fetchJson(url, options = {}) {
  const headers = Object.assign(
    {
      'Content-Type': 'application/json',
      'X-User-Id': localStorage.getItem('userId') || '1' // 临时用户
    },
    options.headers || {}
  );
  const res = await fetch(url, {
    ...options,
    headers,
    credentials: 'include'
  });
  const text = await res.text();
  let data = {};
  try { data = text ? JSON.parse(text) : {}; } catch { data = { raw: text }; }

  return { ok: res.ok, status: res.status, data };
}
