package com.snackstack.server.controller;

/**
 * Common interface for all API controllers
 */
public interface Controller {

  /**
   * Register all routes for this controller
   */
  void registerRoutes();

  /**
   * Get the base path for this controller
   */
  String getBasePath();
}