<?php

interface User {
  public function getProvider();
  public function getId();
  public function getName();
  public function hasFeature($feature);
  public function disconnect();
}