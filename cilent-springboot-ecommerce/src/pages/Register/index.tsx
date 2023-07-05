import { Container, Typography } from "@material-ui/core";
import { Box, Button } from "@mui/material";
import { useFormik } from "formik";
import { useDispatch } from "react-redux";
import TextInput from "../../components/TextInput";
import registerForm from "../../forms/registerForm";
import { showSuccess } from "../../utils/showSuccess";
import { useNavigate } from "react-router-dom";
import { RegisterForm } from "../../types/user";
import { api } from "../../api/axios";
import { useState } from "react";
import { showError } from "../../utils/showError";

function Register() {
  const navigate = useNavigate();
  const dispatch = useDispatch<any>();
  const [loading, setLoading] = useState(false);

  const form = useFormik({
    ...registerForm,
    onSubmit: (values) => {
      delete values.passwordConfirm;
      console.log(values);
      register(values);
    },
  });

  const register = async (creds: RegisterForm) => {
    setLoading(true);
    try {
      const { data } = await api.post("/user/register", creds);
      showSuccess("You have successfully registred!");
      navigate("/login");
    } catch (error: any) {
      showError(error.response?.data?.message ?? "Something went wrong");
    } finally {
      setLoading(false);
    }
  };
  return (
    
    <div style={{backgroundImage: "url(/ogb.jpg)" ,height:'88vh'}}>
    <Container maxWidth="sm" style={{ marginTop: "1rem",  
        backgroundImage: `url(${"/ogb.jpg"})`,
        backgroundPosition: 'center',
        backgroundSize: 'cover',
        backgroundRepeat: 'no-repeat',
        width: '100vw',
        height: '88vh'
    }} >
       <Box sx={{ display: "flex" ,border:"ActiveBorder 1px" }}>
      <img src="elogo.png" alt="Girl in a jacket" width="160" height="160" style={{margin: "-3rem" ,marginLeft: '0.8rem'}}></img>  
        <Typography   variant="h2" align="center" style={{marginLeft: '4rem'}} >
       <div style={{color:"#002699"}}> Register</div>
        
     </Typography></Box>
      <form onSubmit={form.handleSubmit}>
        <TextInput name="firstName" label="First Name" form={form} />
        <TextInput name="lastName" label="Last Name" form={form} />
        <TextInput name="email" label="Email" form={form} />
        <TextInput
          name="password"
          label="Password"
          form={form}
          type="password"
        />
        <TextInput
          name="passwordConfirm"
          label="Password Confirm"
          form={form}
          type="password"
        />
        <Button
          color="primary"
          variant="contained"
          fullWidth
          type="submit"
          disabled={loading}
          style={{marginTop:40,backgroundColor: "#cc2900", color: "#FAF7ED" }}
          
        >
          Register
        </Button>
      </form>
    </Container>
    </div>
  );
}

export default Register;
