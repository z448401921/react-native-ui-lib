import _ from 'lodash';
import PropTypes from 'prop-types';
import React from 'react';
import {PanResponder, Animated} from 'react-native';
import {Constants} from '../../helpers';
import {BaseComponent} from '../../commons';


const DIRECTIONS = {
  UP: 'up',
  DOWN: 'down',
  LEFT: 'left',
  RIGHT: 'right'
};
const SWIPE_VELOCITY = 1.8;
const SPEED = 20;
const BOUNCINESS = 6;

/**
 * @description: PanGestureView component for drag and swipe gestures 
 * (supports vertical (up || down) and horizontal (left || right))
 */
export default class PanGestureView extends BaseComponent {
  static displayName = 'PanGestureView'
  static propTypes = {
    /**
     * onDismiss callback
     */
    onDismiss: PropTypes.func,
    /**
     * The direction of the allowed pan (default is down)
     */
    direction: PropTypes.oneOf(Object.values(DIRECTIONS)),
    /**
     * The delta to animate on swipe instead of dismiss animation
     */
    snapPoint: PropTypes.number
  };

  static defaultProps = {
    direction: DIRECTIONS.DOWN,
    snapPoint: 200
  };
  
  static directions = DIRECTIONS;

  constructor(props) {
    super(props);

    this.state = {
      deltaY: new Animated.Value(0),
      deltaX: new Animated.Value(0)
    };

    this.panResponder = PanResponder.create({
      onMoveShouldSetPanResponder: this.handleMoveShouldSetPanResponder,
      onPanResponderGrant: this.handlePanResponderGrant,
      onPanResponderMove: this.handlePanResponderMove,
      onPanResponderRelease: this.handlePanResponderEnd,
      onPanResponderTerminate: this.handlePanResponderEnd
    });

    this.isHorizontal = (props.direction === DIRECTIONS.LEFT || props.direction === DIRECTIONS.RIGHT);
    this.translate = this.isHorizontal ? this.state.deltaX : this.state.deltaY;
  }

  handleMoveShouldSetPanResponder = (e, gestureState) => {
    // return true if user is swiping, return false if it's a single click
    const {dx, dy} = gestureState;
    return dx > 2 || dx < -2 || dy > 2 || dy < -2;
  };
  handlePanResponderGrant = () => {
    this.swipe = false;
  };
  handlePanResponderMove = (e, gestureState) => {
    const delta = this.isHorizontal ? gestureState.dx : gestureState.dy;
    // console.warn(`delta: ${delta}`);
    const velocity = this.isHorizontal ? gestureState.vx : gestureState.vy;
    
    this.animateMove(delta, velocity);
  };
  handlePanResponderEnd = () => {
    if (!this.swipe) {  
      this.animateEnd();
    } else {
      this.animateSwipe();
    }
  };

  animateMove(delta, velocity) {
    const {direction} = this.getThemeProps();
    const condition = this.isHorizontal ? (direction === DIRECTIONS.LEFT) : (direction === DIRECTIONS.UP);

    if (Math.abs(velocity) >= SWIPE_VELOCITY) {
      // check velocity direction match direction
      if ((condition && velocity < 0) || (!condition && velocity > 0)) {
        // Swipe
        this.swipe = true;
      }
    } else if ((condition && delta < 0) || (!condition && delta > 0)) {
      // Drag
      this.animateDelta(Math.round(delta));
    }
  }
  animateEnd() {
    const {direction, onDismiss} = this.getThemeProps();
    const condition = this.isHorizontal ? (direction === DIRECTIONS.LEFT) : (direction === DIRECTIONS.UP);
    const threshold = this.isHorizontal ? this.layout.width / 2 : this.layout.height / 2;
    const endValue = Math.round(this.translate._value); // eslint-disable-line
    
    if (onDismiss && ((condition && endValue <= -threshold) || (!condition && endValue >= threshold))) {
      this.animateDismiss();
    } else {
      // back to initial position
      this.animateDelta(0);
    }
  }
  animateSwipe() {
    const {onDismiss} = this.getThemeProps();

    if (onDismiss) {
      this.animateDismiss();
    } else {
      this.animateSnapPoint();
    }
  }
  animateSnapPoint() {
    const {direction} = this.getThemeProps();
    const newValue = (direction === DIRECTIONS.UP || direction === DIRECTIONS.LEFT) ?
      -this.props.snapPoint : this.props.snapPoint;
    
    this.animateDelta(newValue);
  }
  animateDelta(toValue) {
    Animated.spring(this.translate, {
      toValue,
      speed: SPEED,
      bounciness: BOUNCINESS
    }).start();
  }
  animateDismiss() {
    const {direction} = this.getThemeProps();
    let translate;
    let newValue = 0;

    if (this.isHorizontal) {
      translate = this.state.deltaX;
      newValue = (direction === DIRECTIONS.LEFT) ? -Constants.screenWidth : Constants.screenWidth;
    } else {
      translate = this.state.deltaY;
      newValue = (direction === DIRECTIONS.UP) ? -Constants.screenHeight : Constants.screenHeight;
    }

    Animated.timing(translate, {
      toValue: Math.round(newValue),
      duration: 280
    }).start(this.onAnimatedFinished);
  }
  onAnimatedFinished = ({finished}) => {
    if (finished) {
      this.onDismiss();
    }
  }
  onDismiss = () => {
    this.initPositions();
    _.invoke(this.props, 'onDismiss');
  }
  initPositions() {
    this.setState({
      deltaY: new Animated.Value(0),
      deltaX: new Animated.Value(0)
    });
  }

  getTransformStyle() {
    return this.isHorizontal ? {transform: [{translateX: this.state.deltaX}]} : {transform: [{translateY: this.state.deltaY}]};
  }

  onLayout = (event) => {
    this.layout = event.nativeEvent.layout;
  }

  render() {
    const {style} = this.getThemeProps();
    const transformStyle = this.getTransformStyle();
    
    return (
      <Animated.View
        style={[
          style,
          transformStyle
        ]} 
        {...this.panResponder.panHandlers}
        onLayout={this.onLayout}
      >
        {this.props.children}
      </Animated.View>
    );
  }
}
