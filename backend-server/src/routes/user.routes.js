const express = require("express");

const router = express.Router();

router.get("/me", (req, res) => {
  res.json({ message: "me" });
});

module.exports = router;
