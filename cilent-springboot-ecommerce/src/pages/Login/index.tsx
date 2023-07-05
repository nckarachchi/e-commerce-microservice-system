import { Container, Typography, colors } from "@material-ui/core";
import { Box, Button } from "@mui/material";
import { useFormik } from "formik";
import { useDispatch, useSelector } from "react-redux";
import TextInput from "../../components/TextInput";
import loginForm from "../../forms/loginForm";
import { AppState } from "../../store";
import { login } from "../../store/actions/userAction";
import { useEffect } from "react";
import { showSuccess } from "../../utils/showSuccess";
import { Link, useNavigate } from "react-router-dom";

function Login() {
  const dispatch = useDispatch<any>();
  const navigate = useNavigate();
  const { data, loading, error } = useSelector((state: AppState) => state.user);
  const { data: user } = useSelector((state: AppState) => state.user);

  useEffect(() => {
    
    if (data.isLogedIn) {
      showSuccess("You have successfully Sign in!");
      

      if (user?.roles?.includes("ROLE_ADMIN")) {
navigate("/admin");
}else{

          navigate("/");
}
        

      
    }
  }, [data]);

  const form = useFormik({
    ...loginForm,
    onSubmit: (values) => {
      dispatch(login(values));
    },
  });



  return (

    <div style={{backgroundImage: "url(/ogb.jpg)" ,height:'75vh'}}>
    <Container maxWidth="sm" style={{ marginTop: "7rem",
       backgroundImage: `url(${"/ogb.jpg"})`,
       backgroundPosition: 'center',
       backgroundSize: 'cover',
       backgroundRepeat: 'no-repeat',
       width: '100vw',
       height: '75vh' }} >
      <form onSubmit={form.handleSubmit}>
      <Box sx={{ display: "flex" ,border:"ActiveBorder 1px" }}>
      <img src="elogo.png" alt="Girl in a jacket" width="160" height="160" style={{margin: "-3rem" ,marginLeft: '0.8rem'}}></img>  
        <Typography   variant="h2" align="center" style={{marginLeft: '4rem'}} >
       <div style={{color:"#002699"}}>SIGN IN</div>
          
        </Typography>
        </Box><div style={{ color: "#FAF7ED", marginTop:"45px" }}>
        <TextInput name="email" label="Email" form={form} />
        <div style={{ color: "#cc2900", marginTop:"15px" }}></div>
        <TextInput
          name="password"
          label="Password"
          form={form}
          type="password"
        />
        <Link
          to="/forgetPassword"
          style={{ float: "right", margin: "0.3rem 0" }}
        >
          Forgot your password?
        </Link></div>
        <Button
        style={{ backgroundColor: "#cc2900", color: "#FAF7ED", marginTop:"40px" }}
          color="primary"
          variant="contained"
          fullWidth
          type="submit"
          disabled={loading}
        >
          SIGN IN
        </Button>
      </form>
    </Container>
    </div>
  );
}

export default Login;
