package com.alexgilleran.hiitme.presentation.programdetail.views;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;

import com.alexgilleran.hiitme.R;
import com.alexgilleran.hiitme.model.EffortLevel;
import com.alexgilleran.hiitme.model.Exercise;
import com.alexgilleran.hiitme.model.ProgramNode;
import com.alexgilleran.hiitme.presentation.programdetail.DragManager;
import com.alexgilleran.hiitme.util.ViewUtils;

public class ProgramNodeView extends DraggableView {
	private ProgramNode programNode;

	private TextView repCountView;
	private ImageButton addExerciseButton;
	private ImageButton addGroupButton;
	private ImageButton moveButton;

	private static final int[] BG_COLOURS = new int[] { 0xFFC5EAF8, 0xFFE2F4FB };

	public ProgramNodeView(Context context) {
		super(context);
	}

	public ProgramNodeView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void onFinishInflate() {
		// if (getContext() instanceof DragPlaceholderProvider) {
		// placeholderProvider = (DragPlaceholderProvider) getContext();
		// }

		this.repCountView = (TextView) this.findViewById(R.id.textview_repcount);

		this.addExerciseButton = (ImageButton) this.findViewById(R.id.button_add_exercise);
		this.addGroupButton = (ImageButton) this.findViewById(R.id.button_add_group);
		this.moveButton = (ImageButton) this.findViewById(R.id.button_move_program_group);

		addGroupButton.setOnClickListener(addGroupListener);
		addExerciseButton.setOnClickListener(addExerciseListener);
		// moveButton.setOnTouchListener(moveListener);

		// this.setOnDragListener(dragListener);
	}

	public void edit() {
		// for (View nodeView : subViews) {
		// nodeView.edit();
		// }
	}

	public void setProgramNode(ProgramNode programNode) {
		this.programNode = programNode;

		render();
	}

	private void render() {
		for (int i = 1; i < getChildCount(); i++) {
			removeViewAt(i);
		}

		for (int i = 0; i < programNode.getChildren().size(); i++) {
			ProgramNode child = programNode.getChildren().get(i);
			DraggableView newView;

			if (child.getAttachedExercise() != null) {
				newView = buildExerciseView(child.getAttachedExercise());
			} else {
				newView = buildProgramNodeView(child);
			}
			newView.initialise(dragManager, this);

			newView.setId(ViewUtils.generateViewId());

			addView(newView);
		}

		repCountView.setText(Integer.toString(programNode.getTotalReps()));
		LayerDrawable background = (LayerDrawable) getBackground().mutate();
		((GradientDrawable) background.findDrawableByLayerId(R.id.card)).setColor(determineBgColour());
		// this.getBackground().setColorFilter(determineBgColour(),
		// Mode.OVERLAY);
		// setBackgroundColor(determineBgColour());
	}

	@Override
	public void initialise(DragManager dragManager, ProgramNodeView parent) {
		super.initialise(dragManager, parent);

		for (int i = 1; i < getChildCount(); i++) {
			((DraggableView) getChildAt(i)).initialise(dragManager, this);
		}
	}

	private int determineBgColour() {
		int colorIndex = programNode.getDepth() % BG_COLOURS.length;
		return BG_COLOURS[colorIndex];
	}

	/**
	 * Populates an existing row meant to contain details of an exercise.
	 * 
	 * @param row
	 *            The {@link TableRow} to populate.
	 * @param exercise
	 *            The {@link Exercise} to source data from.
	 */
	private ExerciseView buildExerciseView(final Exercise exercise) {
		ExerciseView exerciseView = (ExerciseView) layoutInflater.inflate(R.layout.view_exercise, null);

		exerciseView.setNodeView(this);
		exerciseView.setExercise(exercise);

		return exerciseView;
	}

	/**
	 * Populates an existing row meant to contain a {@link ProgramNodeView}.
	 * 
	 * @param row
	 *            The row to populate.
	 * @param node
	 *            The child node to pass to the {@link ProgramNodeView}.
	 */
	private ProgramNodeView buildProgramNodeView(ProgramNode node) {
		ProgramNodeView nodeView = (ProgramNodeView) layoutInflater.inflate(R.layout.view_program_node, null);
		nodeView.setProgramNode(node);

		return nodeView;
	}

	// private OnDragListener dragListener = new OnDragListener() {
	// @Override
	// public boolean onDrag(View reciever, DragEvent event) {
	// int action = event.getAction();
	// float y = event.getY();
	// View draggedView = (View) event.getLocalState();
	//
	// switch (action) {
	// case DragEvent.ACTION_DRAG_STARTED:
	// // do nothing
	// break;
	// case DragEvent.ACTION_DRAG_ENTERED:
	// movePlaceholder(draggedView, event.getY());
	// break;
	// case DragEvent.ACTION_DRAG_EXITED:
	// // ProgramNodeView.this.setOnTouchListener(null);
	// clearPlaceholder(draggedView);
	// break;
	// case DragEvent.ACTION_DROP:
	// clearPlaceholder(draggedView);
	// draggedView = (View) event.getLocalState();
	// // Dropped, reassign View to ViewGroup
	// if (draggedView.getParent() != null) {
	// ViewGroup owner = (ViewGroup) draggedView.getParent();
	// owner.removeView(draggedView);
	// }
	// insertAfter(y, draggedView);
	// // draggedView.setVisibility(View.VISIBLE);
	// break;
	// case DragEvent.ACTION_DRAG_ENDED:
	// clearPlaceholder(draggedView);
	// break;
	// case DragEvent.ACTION_DRAG_LOCATION:
	// movePlaceholder(draggedView, event.getY());
	// break;
	// default:
	// break;
	// }
	// return true;
	// }
	// };

	@Override
	public void removeView(View view) {
		super.removeView(view);
	}

	private final OnClickListener addExerciseListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			programNode.addChildExercise("", 0, EffortLevel.HARD, 5);
			render();
		}
	};

	private final OnClickListener addGroupListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			programNode.addChildNode(1);
			render();
		}
	};

	@Override
	public DraggableView findNextInTree() {
		if (getChildCount() == 1) {
			return parent.findNextAfter(this);
		}

		return (DraggableView) getChildAt(1);
	}

	public DraggableView findNextAfter(DraggableView view) {
		int index = 0;

		// TODO: This is O(clusterfuck), improve it if we can.
		for (int i = 1; i < getChildCount(); i++) {
			if (getChildAt(i) == view) {
				index = i;
			}
		}

		if (index + 1 < getChildCount()) {
			return (DraggableView) getChildAt(index + 1);
		}

		if (parent == null) {
			return null;
		}

		return parent.findNextAfter(this);
	}

	@Override
	public DraggableView findViewAtTop(int top) {
		if (top < getChildAt(0).getTop() + getChildAt(0).getHeight()) {
			return this;
		}

		// TODO: Guess the correct place instead of going top-to-bottom
		for (int i = 1; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if (top >= child.getTop() && top < child.getTop() + child.getHeight()) {
				return ((DraggableView) child).findViewAtTop(top - child.getTop());
			}
		}

		return null;
	}
	// private final OnTouchListener moveListener = new OnTouchListener() {
	// @Override
	// public boolean onTouch(View v, MotionEvent event) {
	// ClipData data = ClipData.newPlainText("", "");
	// DragShadowBuilder shadowBuilder = new View.DragShadowBuilder();
	// startDrag(data, shadowBuilder, ProgramNodeView.this, 0);
	// // ProgramNodeView.this.setVisibility(GONE);
	// if (getParent() != null) {
	// ((ViewGroup) getParent()).removeView(ProgramNodeView.this);
	// }
	// return true;
	// }
	// };
}
