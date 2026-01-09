(function () {
  const PAGE_SIZE = 3;

  function fmtMoney(x) {
    if (x === null || x === undefined) return '0.00';
    const n = Number(x);
    return Number.isFinite(n) ? n.toFixed(2) : String(x);
  }

  async function safeGetCart() {
    try {
      const { data } = await fetchJson(`${API_BASE_URL}/cart`);
      if (data && data.code === 200) return data.data;
    } catch (e) {}
    return null;
  }

  async function refreshBadges() {
  const setBadge = (selector, n) => {
    const $el = $(selector);
    if (!$el.length) return;
    $el.text(n && Number(n) > 0 ? n : '');
  };

  // 未登录清空
  if (!localStorage.getItem('userId')) {
    setBadge('.shopcart .pops', 0);
    setBadge('.wishlist .pops', 0);
    return;
  }

  // 购物车
  try {
    const { data } = await fetchJson(`${API_BASE_URL}/cart/count`);
    if (data && data.code === 200) setBadge('.shopcart .pops', data.data || 0);
  } catch (e) {}

  // 收藏角标
  try {
    const { data } = await fetchJson(`${API_BASE_URL}/wishlist`);
    if (data && data.code === 200) setBadge('.wishlist .pops', (data.data || []).length);
  } catch (e) {}
}


  function renderEmpty() {
    const $wrap = $('#mini-cart-items');
    if (!$wrap.length) return;

    $wrap.html('<div class="text-muted text-center py-3">购物车为空</div>');
    $('.mini-cart-total').text('0');
    $('.mini-cart-subtotal').text('0.00');

    $('#mini-cart-pageinfo').text('1 / 1');
    $('#mini-cart-prev').prop('disabled', true);
    $('#mini-cart-next').prop('disabled', true);

    window.__miniCartState = { items: [], page: 1, pages: 1 };
  }

  function renderPage(items, page, totalQty, totalAmount) {
    const $wrap = $('#mini-cart-items');
    if (!$wrap.length) return;

    const pages = Math.max(1, Math.ceil(items.length / PAGE_SIZE));
    const p = Math.min(Math.max(1, page), pages);

    const start = (p - 1) * PAGE_SIZE;
    const slice = items.slice(start, start + PAGE_SIZE);

    $('.mini-cart-total').text(totalQty ?? items.reduce((s, it) => s + (Number(it.quantity) || 0), 0));
    $('.mini-cart-subtotal').text(fmtMoney(totalAmount ?? 0));

    if (!slice.length) {
      $wrap.html('<div class="text-muted text-center py-3">购物车为空</div>');
    } else {
      $wrap.html(slice.map(it => {
        const img = it.coverImage || 'assets/images/product1.jpg';
        const q = it.quantity ?? 0;
        const price = it.price ?? 0;
        return `
          <a href="product-view.html?id=${it.productId}" class="single_cartdrop mb-3">
            <span class="remove_cart mini-cart-remove" data-itemid="${it.id}" style="cursor:pointer;">
              <i class="las la-times"></i>
            </span>
            <div class="cartdrop_img">
              <img src="${img}" alt="product" style="width:50px;height:50px;object-fit:cover;">
            </div>
            <div class="cartdrop_cont">
              <h5 class="text_lg mb-0 default_link">${it.productName ?? ''}</h5>
              <p class="mb-0 text_xs text_p">x${q} <span class="ms-2">￥${fmtMoney(price)}</span></p>
            </div>
          </a>
        `;
      }).join(''));
    }

    $('#mini-cart-pageinfo').text(`${p} / ${pages}`);
    $('#mini-cart-prev').prop('disabled', p <= 1);
    $('#mini-cart-next').prop('disabled', p >= pages);

    window.__miniCartState = { items, page: p, pages };
  }

  async function loadAndRender(page = 1) {
    const cart = await safeGetCart();
    if (!cart || !Array.isArray(cart.items)) {
      renderEmpty();
      return;
    }
    renderPage(cart.items, page, cart.totalQuantity, cart.totalAmount);
  }

  async function removeItem(itemId) {
    if (!itemId) return;
    try {
      const { data } = await fetchJson(`${API_BASE_URL}/cart/${itemId}`, { method: 'DELETE' });
      if (data && data.code === 200) {
        await refreshBadges();
        const st = window.__miniCartState || { page: 1 };
        await loadAndRender(st.page || 1);
      } else {
        alert(data?.msg || '删除失败');
      }
    } catch (e) {
      alert('删除失败：网络错误');
    }
  }

  function bindEventsOnce() {
    $('.shopcart').off('mouseenter._mc').on('mouseenter._mc', function () {
      const st = window.__miniCartState || { page: 1 };
      loadAndRender(st.page || 1);
    });

    $('#mini-cart-prev').off('click._mc').on('click._mc', function (e) {
      e.preventDefault();
      const st = window.__miniCartState || { page: 1 };
      loadAndRender((st.page || 1) - 1);
    });

    $('#mini-cart-next').off('click._mc').on('click._mc', function (e) {
      e.preventDefault();
      const st = window.__miniCartState || { page: 1 };
      loadAndRender((st.page || 1) + 1);
    });

    $(document).off('click._mcRemove').on('click._mcRemove', '.mini-cart-remove', function (e) {
      e.preventDefault();
      e.stopPropagation();
      removeItem(String($(this).attr('data-itemid')));
    });
  }

  // 自动初始化
  $(document).ready(function () {
    if (!document.getElementById('mini-cart-items')) return;

    refreshBadges();
    bindEventsOnce();
    loadAndRender(1);

    // 导出接口
    window.updateHeaderBadges = refreshBadges;
      window.refreshMiniCart = () => {
      const st = window.__miniCartState || { page: 1 };
      return loadAndRender(st.page || 1);
    };

    // 监听事件
    window.addEventListener('cart:changed', async () => {
      await refreshBadges();
      const st = window.__miniCartState || { page: 1 };
      await loadAndRender(st.page || 1);
    });

    window.addEventListener('wishlist:changed', async () => {
      await refreshBadges();
    });

  })();

})();