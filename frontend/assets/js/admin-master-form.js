requireRole("ADMIN", "login-admin.html");
setAdminGreeting();

const el = (id) => document.getElementById(id);

const params = new URLSearchParams(window.location.search);
const tab = params.get("tab") || "customer";
const editUserId = params.get("userId");
const editProductId = params.get("productId");

function toggleSections() {
  const cust = document.getElementById("customerSection");
  const prod = document.getElementById("productSection");
  if (tab === "product") {
    cust.classList.add("d-none");
    prod.classList.remove("d-none");
  } else {
    prod.classList.add("d-none");
    cust.classList.remove("d-none");
  }
}

toggleSections();

async function prefillCustomer(userId) {
  if (!userId) return;
  const customers = await getJson("/admin/customers");
  const c = customers.find((x) => `${x.userId}` === `${userId}`);
  if (!c) return;
  el("custUserId").value = c.userId;
  el("custName").value = c.name || "";
  el("custEmail").value = c.email || "";
  el("custPhone").value = c.phone || "";
  el("custCompany").value = c.companyName || "";
  el("custActive").checked = !!c.active;
}

async function prefillProduct(productId) {
  if (!productId) return;
  const products = await getJson("/admin/products");
  const p = products.find((x) => `${x.id}` === `${productId}`);
  if (!p) return;
  el("productId").value = p.id;
  el("productName").value = p.name || "";
  el("productSku").value = p.sku || "";
  el("productUnit").value = p.unit || "";
  el("productPrice").value = p.price ?? 0;
  el("productStock").value = p.stock ?? 0;
  el("productActive").checked = !!p.active;
}


el("customerForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const errBox = document.getElementById("customerError");
  if (errBox) errBox.classList.add("d-none");
  const payload = {
    name: el("custName").value.trim(),
    email: el("custEmail").value.trim(),
    phone: el("custPhone").value.trim(),
    password: el("custPassword").value.trim(),
    companyName: el("custCompany").value.trim(),
    active: el("custActive").checked
  };
  const userId = el("custUserId").value;
  let savedUserId = userId;
  if (!payload.name || !payload.email) {
    if (errBox) {
      errBox.textContent = "Nama dan Email wajib diisi";
      errBox.classList.remove("d-none");
    }
    return;
  }
  const updateExisting = async (targetUserId) => {
    const updatePayload = {
      name: payload.name,
      email: payload.email,
      phone: payload.phone,
      companyName: payload.companyName,
      active: payload.active
    };
    await putJson(`/admin/customers/${targetUserId}`, updatePayload);
    savedUserId = targetUserId;
  };

  try {
    if (userId) {
      await updateExisting(userId);
    } else {
      const created = await postJson("/admin/customers", payload);
      savedUserId = created?.userId || userId;
    }

    const addrPayload = {
      label: el("custAddrLabel").value.trim(),
      recipientName: el("custAddrRecipient").value.trim(),
      phone: el("custAddrPhone").value.trim(),
      addressLine: el("custAddrLine").value.trim(),
      city: el("custAddrCity").value.trim(),
      province: el("custAddrProvince").value.trim(),
      postalCode: el("custAddrPostal").value.trim(),
      notes: el("custAddrNotes").value.trim()
    };
    let addrFailed = false;
    let addrErrorMsg = "";
    if (savedUserId && addrPayload.addressLine) {
      try {
        await postJson(`/admin/customers/${savedUserId}/addresses`, addrPayload);
      } catch (addrErr) {
        addrFailed = true;
        addrErrorMsg = addrErr?.message || "Gagal menyimpan alamat.";
      }
    }
    if (addrFailed) {
      if (errBox) {
        errBox.textContent = `Customer tersimpan, alamat gagal: ${addrErrorMsg}`;
        errBox.classList.remove("d-none");
      }
    } else {
      showAlert("customerAlert", "Customer tersimpan");
    }
    setTimeout(() => {
      location.href = "admin-master.html";
    }, 800);
  } catch (err) {
    const msg = err?.message || "Gagal menyimpan customer.";
    const isConflict = msg.includes("Email sudah") || msg.includes("409");
    if (!userId && isConflict) {
      try {
        const customers = await getJson("/admin/customers");
        const email = payload.email.toLowerCase();
        const existing = customers.find((x) => (x.email || "").toLowerCase() === email);
        if (existing?.userId) {
          el("custUserId").value = existing.userId;
          await updateExisting(existing.userId);
          showAlert("customerAlert", "Customer sudah ada, data diperbarui");
          setTimeout(() => {
            location.href = "admin-master.html";
          }, 800);
          return;
        }
      } catch (innerErr) {
        const innerMsg = innerErr?.message || msg;
        if (errBox) {
          errBox.textContent = innerMsg;
          errBox.classList.remove("d-none");
        }
        return;
      }
    }
    if (errBox) {
      errBox.textContent = msg;
      errBox.classList.remove("d-none");
    }
  }
});

el("productForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  const payload = {
    id: el("productId").value || null,
    name: el("productName").value.trim(),
    sku: el("productSku").value.trim(),
    unit: el("productUnit").value.trim(),
    price: Number(el("productPrice").value || 0),
    stock: Number(el("productStock").value || 0),
    active: el("productActive").checked
  };
  if (payload.id) {
    await putJson(`/admin/products/${payload.id}`, payload);
  } else {
    await postJson("/admin/products", payload);
  }
  showAlert("productAlert", "Produk tersimpan");
  setTimeout(() => {
    location.href = "admin-master.html";
  }, 800);
});

Promise.all([prefillCustomer(editUserId), prefillProduct(editProductId)]).catch(console.error);
