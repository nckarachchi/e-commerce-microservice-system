import { Button, Container, TextField, Typography } from "@material-ui/core";
import { AxiosError } from "axios";
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { UserApi } from "../../../api/userApi";
import { showSuccess } from "../../../utils/showSuccess";
import { Box } from "@mui/material";

function ForgetPassword() {
  const navigate = useNavigate();
  const [value, setValue] = useState("");
  const handleChange = (e: any) => {
    setValue(e.target.value);
  };

  const resetPassword = async () => {
    try {
      await UserApi.resetPassword(value);
    } catch (e: any) {
      if (e?.response?.status) {
        showSuccess("new password sent please check your mail!");
        navigate("/login");
      }
    }
  };

  return (

<div style={{backgroundImage: "url(/ogb.jpg)" ,height:'100vh'}}>
    <Container maxWidth="sm" style={{ marginTop: "1rem",  
        backgroundImage: `url(${"/ogb.jpg"})`,
        backgroundPosition: 'center',
        backgroundSize: 'cover',
        backgroundRepeat: 'no-repeat',
        width: '100vw',
        height: '100vh'
        
    }} >
      <Box sx={{ display: "flex" ,border:"ActiveBorder 1px" }}>
      <img src="elogo.png" alt="Girl in a jacket" width="160" height="160" style={{margin: "-3rem" ,marginLeft: '0.8rem'}}></img>  
        <Typography   variant="h2" align="center" style={{marginLeft: '4rem'}} >
       <div style={{color:"#002699"}}> Reset your password?</div>
     </Typography></Box>
     <div style={{ color: "#cc2900", marginTop:"45px" }}></div>
      <TextField
        variant="outlined"
        label="Email"
        fullWidth
        size="small"
        value={value}
        onChange={handleChange}
        style={{ marginTop: "2rem", marginBottom: "0.5rem" }}
        type="email"
      />
      <div style={{ marginTop:"25px" }}></div>
      <Button
        color="primary"
        variant="contained"
        fullWidth
        onClick={resetPassword}
        style={{backgroundColor: "#cc2900", color: "#FAF7ED" }}

      >
        send
      </Button>
      
    </Container>
    </div>
  );
}


export default ForgetPassword;
