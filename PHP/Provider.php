<?php

interface Provider {
  public function getId();
  public function getMarkup();
  public function getScript();
  public function validate($unsafe_request);
  public function checkState();
  public function setCallback($callback);
}