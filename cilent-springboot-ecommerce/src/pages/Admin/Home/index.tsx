import { Typography } from "@material-ui/core";
import React from "react";

function Home() {
  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        justifyContent: "center",
        height: "85vh",
      }}
    >
      <Typography variant="h2" style={{ color: "#990099" }}>-Welcome to Admin panal-</Typography>
      <Typography variant="h4" style={{ color: "#990099" }}>-Please Select Your Action to continue-</Typography>
    </div>
  );
}

export default Home;
