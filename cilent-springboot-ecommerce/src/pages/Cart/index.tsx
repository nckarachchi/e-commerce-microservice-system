import { Box, Button, Container, Typography } from "@material-ui/core";
import { useFormik } from "formik";
import { useEffect, useState } from "react";
import { useMutation } from "react-query";
import { useDispatch, useSelector } from "react-redux";
import { OrderApi } from "../../api/orderApi";
import Card from "../../components/Card";
import Modal from "../../components/Modal";
import SelectInput from "../../components/SelectInput";
import TextInput from "../../components/TextInput";
import orderForm from "../../forms/orderForm";
import { AppState } from "../../store";
import { clearAllItems } from "../../store/actions/cartAction";
import { CreateOrderRequest } from "../../types/order";
import {
  calculateCountOfCartItems,
  calculateTotalPriceOfCartItems,
} from "../../utils/cart";
import { showSuccess } from "../../utils/showSuccess";
import { ProductApi } from "../../api/productApi";
import { showError } from "../../utils/showError";
import { LoadingButton } from "@mui/lab";

function Cart() {
  const items = useSelector((state: AppState) => state.cart);
  const [modalOpen, setModalOpen] = useState(false);
  const [districts, setDistricts] = useState([]);
  const dispatch = useDispatch<any>();
  const form = useFormik({
    ...orderForm,
    onSubmit: (values) => {
      const products = items.map((item) => {
        return { productId: item.product.id, quantity: item.quantity };
      });
      const order = { address: values, items: products } as CreateOrderRequest;
      createMutation.mutate(order);
    },
  });

  useEffect(() => {
    setDistricts(getDistricts(form.values.city));
  }, [form.values.city]);

  const openModal = () => {
    setModalOpen(true);
  };

  const closeModal = () => {
    setModalOpen(false);
  };

  const createMutation = useMutation(OrderApi.createOrder, {
    onSuccess: () => {
      showSuccess("Order has been created successfully");
      form.resetForm();
      dispatch(clearAllItems());
    },
    onError: (e: any) => {
      const res = e.response?.data?.message as string;
      getProducts(res);
    },
  });

  let citiesAndDistrict = require("../../db.json");

  const cities = citiesAndDistrict.map((city: any) => {
    return { name: city.il_adi, id: city.il_adi };
  });

  const getDistricts = (cityName: string) => {
    return citiesAndDistrict
      .find((city: any) => city.il_adi === cityName)
      ?.ilceler.map((district: any) => {
        return { name: district.ilce_adi, id: district.ilce_adi };
      });
  };

  const getProducts = async (res: string) => {
    const productIds = res.substring(1, res.length - 1).split(",") as string[];
    const products = await ProductApi.getProductsByIds(productIds);
    const productNames = products.map((product) => product.name);
    showError(`${productNames} not in stock!`);
  };

  console.log("loading", createMutation.isLoading);

  return (
    <>
      <Container maxWidth="sm">
        {items.map((item) => (
          <Box style={{ margin: "1rem 0" }}>
            <Card key={item.product.id} product={item.product} />
          </Box>
        ))}
      </Container>
      <Typography variant="h5" align="center">
        There are {calculateCountOfCartItems(items)} items and total price is{" "}
        RS.{calculateTotalPriceOfCartItems(items)}
      </Typography>
      <Modal
        open={modalOpen}
        setOpen={setModalOpen}
        title="Please enter your informations"
        disableBtn={true}
      >
        <form onSubmit={form.handleSubmit}>
          <SelectInput name="city" label="city" form={form} data={cities} />
          <SelectInput
            name="district"
            label="district"
            form={form}
            data={districts}
          />
          <TextInput name="addressDetail" label="addressDetail" form={form} />
          <Button
            color="secondary"
            variant="contained"
            fullWidth
            onClick={closeModal}
            style={{ backgroundColor: "#33cc33" }}
          >
            Cancel
          </Button>
          <div style={{ marginTop:"10px" }}></div>
          <Button
            color="primary"
            variant="contained"
            fullWidth
            type="submit"
            onClick={closeModal}
            style={{ backgroundColor: "#e60000" }}
          >
            
            Order

          </Button>
        </form>
      </Modal>
      {items.length > 0 && (
        <LoadingButton
          variant="contained"
          onClick={openModal}
          style={{ margin: "1rem auto", display: "block" }}
          loading={createMutation.isLoading}
        >
          Order
        </LoadingButton>
      )}
    </>
  );
}

export default Cart;
